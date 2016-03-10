package gov.nasa.horizon.security.server

class TokenController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index = {
        redirect(action: "list", params: params)
    }

    def list = {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [tokenInstanceList: Token.list(params), tokenInstanceTotal: Token.count()]
    }

    def create = {
        def tokenInstance = new Token()
        tokenInstance.properties = params
        return [tokenInstance: tokenInstance]
    }

    def save = {
        def tokenInstance = new Token(params)
        if (tokenInstance.save(flush: true)) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'token.label', default: 'Token'), tokenInstance.id])}"
            redirect(action: "show", id: tokenInstance.id)
        }
        else {
            render(view: "create", model: [tokenInstance: tokenInstance])
        }
    }

    def show = {
        def tokenInstance = Token.get(params.id)
        if (!tokenInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'token.label', default: 'Token'), params.id])}"
            redirect(action: "list")
        }
        else {
            [tokenInstance: tokenInstance]
        }
    }

    def edit = {
        def tokenInstance = Token.get(params.id)
        if (!tokenInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'token.label', default: 'Token'), params.id])}"
            redirect(action: "list")
        }
        else {
            return [tokenInstance: tokenInstance]
        }
    }

    def update = {
        def tokenInstance = Token.get(params.id)
        if (tokenInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (tokenInstance.version > version) {
                    
                    tokenInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'token.label', default: 'Token')] as Object[], "Another user has updated this Token while you were editing")
                    render(view: "edit", model: [tokenInstance: tokenInstance])
                    return
                }
            }
            tokenInstance.properties = params
            if (!tokenInstance.hasErrors() && tokenInstance.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'token.label', default: 'Token'), tokenInstance.id])}"
                redirect(action: "show", id: tokenInstance.id)
            }
            else {
                render(view: "edit", model: [tokenInstance: tokenInstance])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'token.label', default: 'Token'), params.id])}"
            redirect(action: "list")
        }
    }

    def delete = {
        def tokenInstance = Token.get(params.id)
        if (tokenInstance) {
            try {
                tokenInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'token.label', default: 'Token'), params.id])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'token.label', default: 'Token'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'token.label', default: 'Token'), params.id])}"
            redirect(action: "list")
        }
    }
}
