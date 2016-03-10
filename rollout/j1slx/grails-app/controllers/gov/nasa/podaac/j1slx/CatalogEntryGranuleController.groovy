package gov.nasa.podaac.j1slx

class CatalogEntryGranuleController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index = {
        redirect(action: "list", params: params)
    }

    def list = {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [catalogEntryGranuleInstanceList: CatalogEntryGranule.list(params), catalogEntryGranuleInstanceTotal: CatalogEntryGranule.count()]
    }

    def create = {
        def catalogEntryGranuleInstance = new CatalogEntryGranule()
        catalogEntryGranuleInstance.properties = params
        return [catalogEntryGranuleInstance: catalogEntryGranuleInstance]
    }

    def save = {
        def catalogEntryGranuleInstance = new CatalogEntryGranule(params)
        if (catalogEntryGranuleInstance.save(flush: true)) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'catalogEntryGranule.label', default: 'CatalogEntryGranule'), catalogEntryGranuleInstance.id])}"
            redirect(action: "show", id: catalogEntryGranuleInstance.id)
        }
        else {
            render(view: "create", model: [catalogEntryGranuleInstance: catalogEntryGranuleInstance])
        }
    }

    def show = {
        def catalogEntryGranuleInstance = CatalogEntryGranule.get(params.id)
        if (!catalogEntryGranuleInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'catalogEntryGranule.label', default: 'CatalogEntryGranule'), params.id])}"
            redirect(action: "list")
        }
        else {
            [catalogEntryGranuleInstance: catalogEntryGranuleInstance]
        }
    }

    def edit = {
        def catalogEntryGranuleInstance = CatalogEntryGranule.get(params.id)
        if (!catalogEntryGranuleInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'catalogEntryGranule.label', default: 'CatalogEntryGranule'), params.id])}"
            redirect(action: "list")
        }
        else {
            return [catalogEntryGranuleInstance: catalogEntryGranuleInstance]
        }
    }

    def update = {
        def catalogEntryGranuleInstance = CatalogEntryGranule.get(params.id)
        if (catalogEntryGranuleInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (catalogEntryGranuleInstance.version > version) {
                    
                    catalogEntryGranuleInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'catalogEntryGranule.label', default: 'CatalogEntryGranule')] as Object[], "Another user has updated this CatalogEntryGranule while you were editing")
                    render(view: "edit", model: [catalogEntryGranuleInstance: catalogEntryGranuleInstance])
                    return
                }
            }
            catalogEntryGranuleInstance.properties = params
            if (!catalogEntryGranuleInstance.hasErrors() && catalogEntryGranuleInstance.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'catalogEntryGranule.label', default: 'CatalogEntryGranule'), catalogEntryGranuleInstance.id])}"
                redirect(action: "show", id: catalogEntryGranuleInstance.id)
            }
            else {
                render(view: "edit", model: [catalogEntryGranuleInstance: catalogEntryGranuleInstance])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'catalogEntryGranule.label', default: 'CatalogEntryGranule'), params.id])}"
            redirect(action: "list")
        }
    }

    def delete = {
        def catalogEntryGranuleInstance = CatalogEntryGranule.get(params.id)
        if (catalogEntryGranuleInstance) {
            try {
                catalogEntryGranuleInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'catalogEntryGranule.label', default: 'CatalogEntryGranule'), params.id])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'catalogEntryGranule.label', default: 'CatalogEntryGranule'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'catalogEntryGranule.label', default: 'CatalogEntryGranule'), params.id])}"
            redirect(action: "list")
        }
    }
}
