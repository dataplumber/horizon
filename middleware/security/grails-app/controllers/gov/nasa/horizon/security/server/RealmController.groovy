package gov.nasa.horizon.security.server

class RealmController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index = {
        redirect(action: "list", params: params)
    }

    def list = {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [realmInstanceList: Realm.list(params), realmInstanceTotal: Realm.count()]
    }

    def create = {
        def realmInstance = new Realm()
        realmInstance.properties = params
        return [realmInstance: realmInstance]
    }

    def save = {
        def realmInstance = new Realm(params)
        if (realmInstance.save(flush: true)) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'realm.label', default: 'Realm'), realmInstance.id])}"
            redirect(action: "show", id: realmInstance.id)
        }
        else {
            render(view: "create", model: [realmInstance: realmInstance])
        }
    }

    def show = {
        def realmInstance = Realm.get(params.id)
        if (!realmInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'realm.label', default: 'Realm'), params.id])}"
            redirect(action: "list")
        }
        else {
            [realmInstance: realmInstance]
        }
    }

    def edit = {
        def realmInstance = Realm.get(params.id)
        if (!realmInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'realm.label', default: 'Realm'), params.id])}"
            redirect(action: "list")
        }
        else {
            return [realmInstance: realmInstance]
        }
    }

    def update = {
        def realmInstance = Realm.get(params.id)
        if (realmInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (realmInstance.version > version) {
                    
                    realmInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'realm.label', default: 'Realm')] as Object[], "Another user has updated this Realm while you were editing")
                    render(view: "edit", model: [realmInstance: realmInstance])
                    return
                }
            }
            realmInstance.properties = params
            if (!realmInstance.hasErrors() && realmInstance.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'realm.label', default: 'Realm'), realmInstance.id])}"
                redirect(action: "show", id: realmInstance.id)
            }
            else {
                render(view: "edit", model: [realmInstance: realmInstance])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'realm.label', default: 'Realm'), params.id])}"
            redirect(action: "list")
        }
    }

    def delete = {
        def realmInstance = Realm.get(params.id)
        if (realmInstance) {
            try {
                realmInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'realm.label', default: 'Realm'), params.id])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'realm.label', default: 'Realm'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'realm.label', default: 'Realm'), params.id])}"
            redirect(action: "list")
        }
    }
}
