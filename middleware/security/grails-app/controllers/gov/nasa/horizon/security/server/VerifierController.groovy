package gov.nasa.horizon.security.server

class VerifierController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index = {
        redirect(action: "list", params: params)
    }

    def list = {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [verifierInstanceList: Verifier.list(params), verifierInstanceTotal: Verifier.count()]
    }

    def create = {
        def verifierInstance = new Verifier()
        verifierInstance.properties = params
        return [verifierInstance: verifierInstance]
    }

    def save = {
        def verifierInstance = new Verifier(params)
        if (verifierInstance.save(flush: true)) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'verifier.label', default: 'Verifier'), verifierInstance.id])}"
            redirect(action: "show", id: verifierInstance.id)
        }
        else {
            render(view: "create", model: [verifierInstance: verifierInstance])
        }
    }

    def show = {
        def verifierInstance = Verifier.get(params.id)
        if (!verifierInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'verifier.label', default: 'Verifier'), params.id])}"
            redirect(action: "list")
        }
        else {
            [verifierInstance: verifierInstance]
        }
    }

    def edit = {
        def verifierInstance = Verifier.get(params.id)
        if (!verifierInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'verifier.label', default: 'Verifier'), params.id])}"
            redirect(action: "list")
        }
        else {
            return [verifierInstance: verifierInstance]
        }
    }

    def update = {
        def verifierInstance = Verifier.get(params.id)
        if (verifierInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (verifierInstance.version > version) {
                    
                    verifierInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'verifier.label', default: 'Verifier')] as Object[], "Another user has updated this Verifier while you were editing")
                    render(view: "edit", model: [verifierInstance: verifierInstance])
                    return
                }
            }
            verifierInstance.properties = params
            if (!verifierInstance.hasErrors() && verifierInstance.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'verifier.label', default: 'Verifier'), verifierInstance.id])}"
                redirect(action: "show", id: verifierInstance.id)
            }
            else {
                render(view: "edit", model: [verifierInstance: verifierInstance])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'verifier.label', default: 'Verifier'), params.id])}"
            redirect(action: "list")
        }
    }

    def delete = {
        def verifierInstance = Verifier.get(params.id)
        if (verifierInstance) {
            try {
                verifierInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'verifier.label', default: 'Verifier'), params.id])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'verifier.label', default: 'Verifier'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'verifier.label', default: 'Verifier'), params.id])}"
            redirect(action: "list")
        }
    }
}
