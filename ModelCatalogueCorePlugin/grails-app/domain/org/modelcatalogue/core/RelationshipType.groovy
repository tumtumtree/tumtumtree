package org.modelcatalogue.core

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import com.google.common.util.concurrent.UncheckedExecutionException
import grails.util.GrailsNameUtils
import org.apache.log4j.Logger
import org.modelcatalogue.core.util.SecuredRuleExecutor

import java.util.concurrent.Callable

class RelationshipType {

    private static final Cache<String, Long> typesCache = CacheBuilder.newBuilder().initialCapacity(20).build()

    def relationshipTypeService

    //name of the relationship type i.e. parentChild  or synonym
    String name

    // system relationship types are not returned from the controller
    Boolean system = false

    //the both sides of the relationship ie. for parentChild this would be parent (for synonym this is synonym, so the same on both sides)
    String sourceToDestination

    // detailed explanation of the source to destination relationship
    String sourceToDestinationDescription

    //the both sides of the relationship i.e. for parentChild this would be child (for synonym this is synonym, so the same on both sides)
    String destinationToSource

    // detailed explanation of the reversed relationship
    String destinationToSourceDescription

    //you can constrain the relationship type
    Class sourceClass

    // you can constrain the relationship type
    Class destinationClass

    // comma separated list of metadata hints
    String metadataHints

    // if the direction of the relationship doesn't matter
    Boolean bidirectional = Boolean.FALSE

    /** if relationships of this type shall not be carried over when new draft version is created */
    Boolean versionSpecific = Boolean.FALSE

    SecuredRuleExecutor.ReusableScript ruleScript

    /**
     * This is a script which will be evaluated with following binding:
     * source
     * destination
     * type
     *
     * Type stands for current type evaluated.
     *
     * For the beginning there are no constraints for the scripts so use them carefully.
     *
     */
    String rule

    static constraints = {
        def classValidator = { val, obj ->
            if (!val) return true
            if (!CatalogueElement.isAssignableFrom(val)) return "Only org.modelcatalogue.core.CatalogueElement child classes are allowed"
            return true
        }
        name unique: true, maxSize: 255, matches: /[a-z\-0-9A-Z]+/
        sourceToDestination maxSize: 255
        destinationToSource maxSize: 255
        sourceClass validator: classValidator
        destinationClass validator: classValidator
        metadataHints nullable: true, maxSize: 10000
        rule nullable: true, maxSize: 10000
        sourceToDestinationDescription nullable: true, maxSize: 2000
        destinationToSourceDescription nullable: true, maxSize: 2000
    }

    static transients = ['ruleScript']


    static mapping = {
        cache 'nonstrict-read-write'
        sort "name"
		name index: 'RelationType_name_idx'
		destinationClass index: 'RelationType_destinationClass_idx'
    }

    void setRule(String rule) {
        this.rule = rule
        this.ruleScript = null
    }

    def validateSourceDestination(CatalogueElement source, CatalogueElement destination, Map<String, String> ext) {

        if (!sourceClass.isInstance(source)) {
            return 'source.not.instance.of'
        }

        if (!destinationClass.isInstance(destination)) {
            return 'destination.not.instance.of'
        }

        if (rule && rule.trim()) {
            def result = validateRule(source, destination, ext)
            if (result instanceof List && result.size() > 1 && result.first() instanceof String) {
                return result
            }

            if (result instanceof CharSequence) {
                return result
            }
            if (result instanceof Boolean && !result) {
                return 'rule.did.not.pass'
            }

            if ((result instanceof Boolean && result) || result == null) {
                return null
            }

            if (result instanceof Throwable) {
                log.warn("Rule of $name thrown an exception for $source and $destination: $result", result)
                return ['rule.did.not.pass.with.exception', [result.toString()] as Object[], "Rule thrown an exception: $result.message"]
            }

            if (result) {
                log.warn("Rule returned value which is not String or Boolean, this is very likely a bug. Result: $result")
            }
        }

        return null
    }

    def validateRule(CatalogueElement source, CatalogueElement destination, Map<String, String> ext) {
        if (!rule || !rule.trim()) {
            return true
        }

        if (!ruleScript) {
            ruleScript = new SecuredRuleExecutor(
                source: source,
                destination: destination,
                type: this,
                ext: ext
            ).reuse(rule)
        }

        ruleScript.execute(
            source: source,
            destination: destination,
            type: this,
            ext: ext
        )
    }


    static getContainmentType() {
        readByName("containment")
    }

    static getClassificationType() {
        readByName("classification")
    }

    static getFavouriteType() {
        readByName("favourite")
    }

    static getSynonymType() {
        readByName("synonym")
    }

    static getRelatedToType() {
        readByName("relatedTo")
    }

    static getHierarchyType() {
        readByName("hierarchy")
    }

    static getSupersessionType() {
        readByName("supersession")
    }

    static getBaseType() {
        readByName("base")
    }

    static readByName(String name) {
        try {
            Long id = typesCache.get(name, { ->
                RelationshipType type = RelationshipType.findByName(name, [cache: true, readOnly: true])
                if (!type) {
                    throw new IllegalArgumentException("Type '$name' does not exist!")
                }
                return type.id
            } as Callable<Long>)
            RelationshipType.get(id)

        } catch (UncheckedExecutionException e) {
            if (e.cause instanceof IllegalArgumentException) {
                Logger.getLogger(RelationshipType).warn "Type '$name' requested but not found!"
            } else {
                throw e
            }
        }
    }

    String toString() {
        "${getClass().simpleName}[id: ${id}, name: ${name}]"
    }

    Map<String, Object> getInfo() {
        [
                id: id,
                name: name,
                link: "/${GrailsNameUtils.getPropertyName(getClass())}/$id"
        ]
    }

    def beforeInsert() {
        relationshipTypeService.clearCache()
    }

    def afterInsert() {
        typesCache.put name, getId()
    }

    def beforeUpdate() {
        relationshipTypeService.clearCache()
        typesCache.invalidate(name)
    }

    def afterUpdate() {
        typesCache.put name, getId()
    }

    def beforeDelete() {
        relationshipTypeService.clearCache()
        typesCache.invalidate(name)
    }


    static String toCamelCase(String text) {
        if (!text) return text
        def newParts = []
        text.split(/\s+/).eachWithIndex { it, index ->
            if (index > 0) {
                newParts << it.capitalize()
            } else {
                newParts << it
            }
        }
        newParts.join('')
    }
}



