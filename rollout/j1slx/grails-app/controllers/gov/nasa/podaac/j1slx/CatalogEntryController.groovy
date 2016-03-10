package gov.nasa.podaac.j1slx

class CatalogEntryController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index = {
        redirect(action: "list", params: params)
    }

    def list = {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [catalogEntryInstanceList: CatalogEntry.list(params), catalogEntryInstanceTotal: CatalogEntry.count()]
    }

    def create = {
        def catalogEntryInstance = new CatalogEntry()
        catalogEntryInstance.properties = params
        return [catalogEntryInstance: catalogEntryInstance]
    }

    def save = {
        def catalogEntryInstance = new CatalogEntry(params)
        if (catalogEntryInstance.save(flush: true)) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'catalogEntry.label', default: 'CatalogEntry'), catalogEntryInstance.id])}"
            redirect(action: "show", id: catalogEntryInstance.id)
        }
        else {
            render(view: "create", model: [catalogEntryInstance: catalogEntryInstance])
        }
    }

    def show = {
        def catalogEntryInstance = CatalogEntry.get(params.id)
        if (!catalogEntryInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'catalogEntry.label', default: 'CatalogEntry'), params.id])}"
            redirect(action: "list")
        }
        else {
            [catalogEntryInstance: catalogEntryInstance]
        }
    }

    def edit = {
        def catalogEntryInstance = CatalogEntry.get(params.id)
        if (!catalogEntryInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'catalogEntry.label', default: 'CatalogEntry'), params.id])}"
            redirect(action: "list")
        }
        else {
            return [catalogEntryInstance: catalogEntryInstance]
        }
    }

    def update = {
        def catalogEntryInstance = CatalogEntry.get(params.id)
        if (catalogEntryInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (catalogEntryInstance.version > version) {
                    
                    catalogEntryInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'catalogEntry.label', default: 'CatalogEntry')] as Object[], "Another user has updated this CatalogEntry while you were editing")
                    render(view: "edit", model: [catalogEntryInstance: catalogEntryInstance])
                    return
                }
            }
            catalogEntryInstance.properties = params
            if (!catalogEntryInstance.hasErrors() && catalogEntryInstance.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'catalogEntry.label', default: 'CatalogEntry'), catalogEntryInstance.id])}"
                redirect(action: "show", id: catalogEntryInstance.id)
            }
            else {
                render(view: "edit", model: [catalogEntryInstance: catalogEntryInstance])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'catalogEntry.label', default: 'CatalogEntry'), params.id])}"
            redirect(action: "list")
        }
    }

    def delete = {
        def catalogEntryInstance = CatalogEntry.get(params.id)
        if (catalogEntryInstance) {
            try {
                catalogEntryInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'catalogEntry.label', default: 'CatalogEntry'), params.id])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'catalogEntry.label', default: 'CatalogEntry'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'catalogEntry.label', default: 'CatalogEntry'), params.id])}"
            redirect(action: "list")
        }
    }
}
