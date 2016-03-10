package gov.nasa.horizon.security.server

class IngAccessRoleController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index = {
        redirect(action: "list", params: params)
    }

    def list = {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [ingAccessRoleInstanceList: IngAccessRole.list(params), ingAccessRoleInstanceTotal: IngAccessRole.count()]
    }

    def create = {
        def ingAccessRoleInstance = new IngAccessRole()
        ingAccessRoleInstance.properties = params
        return [ingAccessRoleInstance: ingAccessRoleInstance]
    }

    def save = {
        def ingAccessRoleInstance = new IngAccessRole(params)
        if (ingAccessRoleInstance.save(flush: true)) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'ingAccessRole.label', default: 'IngAccessRole'), ingAccessRoleInstance.id])}"
            redirect(action: "show", id: ingAccessRoleInstance.id)
        }
        else {
            render(view: "create", model: [ingAccessRoleInstance: ingAccessRoleInstance])
        }
    }

    def show = {
        def ingAccessRoleInstance = IngAccessRole.get(params.id)
        if (!ingAccessRoleInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'ingAccessRole.label', default: 'IngAccessRole'), params.id])}"
            redirect(action: "list")
        }
        else {
            [ingAccessRoleInstance: ingAccessRoleInstance]
        }
    }

    def edit = {
        def ingAccessRoleInstance = IngAccessRole.get(params.id)
        if (!ingAccessRoleInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'ingAccessRole.label', default: 'IngAccessRole'), params.id])}"
            redirect(action: "list")
        }
        else {
            return [ingAccessRoleInstance: ingAccessRoleInstance]
        }
    }

    def update = {
        def ingAccessRoleInstance = IngAccessRole.get(params.id)
        if (ingAccessRoleInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (ingAccessRoleInstance.version > version) {
                    
                    ingAccessRoleInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'ingAccessRole.label', default: 'IngAccessRole')] as Object[], "Another user has updated this IngAccessRole while you were editing")
                    render(view: "edit", model: [ingAccessRoleInstance: ingAccessRoleInstance])
                    return
                }
            }
            ingAccessRoleInstance.properties = params
            if (!ingAccessRoleInstance.hasErrors() && ingAccessRoleInstance.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'ingAccessRole.label', default: 'IngAccessRole'), ingAccessRoleInstance.id])}"
                redirect(action: "show", id: ingAccessRoleInstance.id)
            }
            else {
                render(view: "edit", model: [ingAccessRoleInstance: ingAccessRoleInstance])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'ingAccessRole.label', default: 'IngAccessRole'), params.id])}"
            redirect(action: "list")
        }
    }

    def delete = {
        def ingAccessRoleInstance = IngAccessRole.get(params.id)
        if (ingAccessRoleInstance) {
            try {
                ingAccessRoleInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'ingAccessRole.label', default: 'IngAccessRole'), params.id])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'ingAccessRole.label', default: 'IngAccessRole'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'ingAccessRole.label', default: 'IngAccessRole'), params.id])}"
            redirect(action: "list")
        }
    }
}
