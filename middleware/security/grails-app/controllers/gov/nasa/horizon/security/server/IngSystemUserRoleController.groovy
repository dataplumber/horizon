package gov.nasa.horizon.security.server

class IngSystemUserRoleController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index = {
        redirect(action: "list", params: params)
    }

    def list = {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [ingSystemUserRoleInstanceList: IngSystemUserRole.list(params), ingSystemUserRoleInstanceTotal: IngSystemUserRole.count()]
    }

    def create = {
        def ingSystemUserRoleInstance = new IngSystemUserRole()
        ingSystemUserRoleInstance.properties = params
        return [ingSystemUserRoleInstance: ingSystemUserRoleInstance]
    }

    def save = {
        def ingSystemUserRoleInstance = new IngSystemUserRole(params)
        if (ingSystemUserRoleInstance.save(flush: true)) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'ingSystemUserRole.label', default: 'IngSystemUserRole'), ingSystemUserRoleInstance.id])}"
            redirect(action: "show", id: ingSystemUserRoleInstance.id)
        }
        else {
            render(view: "create", model: [ingSystemUserRoleInstance: ingSystemUserRoleInstance])
        }
    }

    def show = {
        def ingSystemUserRoleInstance = IngSystemUserRole.get(params.id)
        if (!ingSystemUserRoleInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'ingSystemUserRole.label', default: 'IngSystemUserRole'), params.id])}"
            redirect(action: "list")
        }
        else {
            [ingSystemUserRoleInstance: ingSystemUserRoleInstance]
        }
    }

    def edit = {
        def ingSystemUserRoleInstance = IngSystemUserRole.get(params.id)
        if (!ingSystemUserRoleInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'ingSystemUserRole.label', default: 'IngSystemUserRole'), params.id])}"
            redirect(action: "list")
        }
        else {
            return [ingSystemUserRoleInstance: ingSystemUserRoleInstance]
        }
    }

    def update = {
        def ingSystemUserRoleInstance = IngSystemUserRole.get(params.id)
        if (ingSystemUserRoleInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (ingSystemUserRoleInstance.version > version) {
                    
                    ingSystemUserRoleInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'ingSystemUserRole.label', default: 'IngSystemUserRole')] as Object[], "Another user has updated this IngSystemUserRole while you were editing")
                    render(view: "edit", model: [ingSystemUserRoleInstance: ingSystemUserRoleInstance])
                    return
                }
            }
            ingSystemUserRoleInstance.properties = params
            if (!ingSystemUserRoleInstance.hasErrors() && ingSystemUserRoleInstance.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'ingSystemUserRole.label', default: 'IngSystemUserRole'), ingSystemUserRoleInstance.id])}"
                redirect(action: "show", id: ingSystemUserRoleInstance.id)
            }
            else {
                render(view: "edit", model: [ingSystemUserRoleInstance: ingSystemUserRoleInstance])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'ingSystemUserRole.label', default: 'IngSystemUserRole'), params.id])}"
            redirect(action: "list")
        }
    }

    def delete = {
        def ingSystemUserRoleInstance = IngSystemUserRole.get(params.id)
        if (ingSystemUserRoleInstance) {
            try {
                ingSystemUserRoleInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'ingSystemUserRole.label', default: 'IngSystemUserRole'), params.id])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'ingSystemUserRole.label', default: 'IngSystemUserRole'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'ingSystemUserRole.label', default: 'IngSystemUserRole'), params.id])}"
            redirect(action: "list")
        }
    }
}
