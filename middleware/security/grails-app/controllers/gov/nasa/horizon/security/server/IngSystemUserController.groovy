package gov.nasa.horizon.security.server

import gov.nasa.horizon.security.server.utils.Encrypt

class IngSystemUserController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index = {
        redirect(action: "list", params: params)
    }

    def list = {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [ingSystemUserInstanceList: IngSystemUser.list(params), ingSystemUserInstanceTotal: IngSystemUser.count()]
    }

    def create = {
        def ingSystemUserInstance = new IngSystemUser()
        ingSystemUserInstance.properties = params
        return [ingSystemUserInstance: ingSystemUserInstance]
    }

    def save = {
        def ingSystemUserInstance = new IngSystemUser(params)
		ingSystemUserInstance.password = Encrypt.encrypt(ingSystemUserInstance.password)
		
        if (ingSystemUserInstance.save(flush: true)) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'ingSystemUser.label', default: 'IngSystemUser'), ingSystemUserInstance.id])}"
            redirect(action: "show", id: ingSystemUserInstance.id)
        }
        else {
            render(view: "create", model: [ingSystemUserInstance: ingSystemUserInstance])
        }
    }

    def show = {
        def ingSystemUserInstance = IngSystemUser.get(params.id)
        if (!ingSystemUserInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'ingSystemUser.label', default: 'IngSystemUser'), params.id])}"
            redirect(action: "list")
        }
        else {
            [ingSystemUserInstance: ingSystemUserInstance]
        }
    }

    def edit = {
        def ingSystemUserInstance = IngSystemUser.get(params.id)
        if (!ingSystemUserInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'ingSystemUser.label', default: 'IngSystemUser'), params.id])}"
            redirect(action: "list")
        }
        else {
            return [ingSystemUserInstance: ingSystemUserInstance]
        }
    }

	def changePassword = {
		def ingSystemUserInstance = IngSystemUser.findByName(session.user)
		if (!ingSystemUserInstance) {
			flash.message = "Could not find user in database with name: '${session.user}'"
			redirect(uri:'/')
		}
		else {
			return [ingSystemUserInstance: ingSystemUserInstance]
		}
	}
	
	def resetPassword = {
		def ingSystemUserInstance = IngSystemUser.get(params.id)
		if (!ingSystemUserInstance) {
			flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'ingSystemUser.label', default: 'IngSystemUser'), params.id])}"
			redirect(action: "list")
		}
		else {
			return [ingSystemUserInstance: ingSystemUserInstance]
		}
	}
	
	def updatePass = {
		def ingSystemUserInstance = IngSystemUser.get(params.id)
		def oldPass = ingSystemUserInstance.password
		if (ingSystemUserInstance) {
			if (params.version) {
				def version = params.version.toLong()
				if (ingSystemUserInstance.version > version) {
					
					ingSystemUserInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'ingSystemUser.label', default: 'IngSystemUser')] as Object[], "Another user has updated this IngSystemUser while you were editing")
					render(view: "changePassword", model: [ingSystemUserInstance: ingSystemUserInstance])
					return
				}
			}
			if(Encrypt.encrypt(params.passwordCurrent) == oldPass){
				log.debug('Correct current password entered.')
			}
			else{
				ingSystemUserInstance.errors.rejectValue("password", null, [message(code: 'ingSystemUser.label', default: 'IngSystemUser')] as Object[], "The password entered for 'Current Password' is incorrect.")
				render(view: "changePassword", model: [ingSystemUserInstance: ingSystemUserInstance])
				return;
			}
			if(params.password == params.passwordConfirm)
				ingSystemUserInstance.password = Encrypt.encrypt(params.password)
			else{
				ingSystemUserInstance.errors.rejectValue("password", null, [message(code: 'ingSystemUser.label', default: 'IngSystemUser')] as Object[], "Password mismatch. Please enter the same value for password and confirm password.")
				render(view: "changePassword", model: [ingSystemUserInstance: ingSystemUserInstance])
				return;
			}
			
			if (!ingSystemUserInstance.hasErrors() && ingSystemUserInstance.save(flush: true)) {
				flash.message = "Password successfully updated for user '${ingSystemUserInstance.name}'."
				redirect(uri:'/')
				//redirect(action: "show", id: ingSystemUserInstance.id)
			}
			else {
				redirect(uri:'/')
				//render(view: "edit", model: [ingSystemUserInstance: ingSystemUserInstance])
			}
		}
		else {
			flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'ingSystemUser.label', default: 'IngSystemUser'), params.id])}"
			redirect(action: "list")
		}
	}
	
	
	def resetPass = {
		def ingSystemUserInstance = IngSystemUser.get(params.id)
		def oldPass = ingSystemUserInstance.password
		if (ingSystemUserInstance) {
			if (params.version) {
				def version = params.version.toLong()
				if (ingSystemUserInstance.version > version) {
					
					ingSystemUserInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'ingSystemUser.label', default: 'IngSystemUser')] as Object[], "Another user has updated this IngSystemUser while you were editing")
					render(view: "resetPassword", model: [ingSystemUserInstance: ingSystemUserInstance])
					return
				}
			}
			if(params.password == params.passwordConfirm)
				ingSystemUserInstance.password = Encrypt.encrypt(params.password)
			else{
				ingSystemUserInstance.errors.rejectValue("password", null, [message(code: 'ingSystemUser.label', default: 'IngSystemUser')] as Object[], "Password mismatch. Please enter the same value for password and confirm password.")
				render(view: "resetPassword", model: [ingSystemUserInstance: ingSystemUserInstance])
				return;
			}
			
			if (!ingSystemUserInstance.hasErrors() && ingSystemUserInstance.save(flush: true)) {
				flash.message = "${message(code: 'default.updated.message', args: [message(code: 'ingSystemUser.label', default: 'IngSystemUser'), ingSystemUserInstance.id])}"
				redirect(action: "show", id: ingSystemUserInstance.id)
			}
			else {
				render(view: "edit", model: [ingSystemUserInstance: ingSystemUserInstance])
			}
		}
		else {
			flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'ingSystemUser.label', default: 'IngSystemUser'), params.id])}"
			redirect(action: "list")
		}
	}
	
	
	
    def update = {
        def ingSystemUserInstance = IngSystemUser.get(params.id)
		def oldPass = ingSystemUserInstance.password
        if (ingSystemUserInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (ingSystemUserInstance.version > version) {
                    
                    ingSystemUserInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'ingSystemUser.label', default: 'IngSystemUser')] as Object[], "Another user has updated this IngSystemUser while you were editing")
                    render(view: "edit", model: [ingSystemUserInstance: ingSystemUserInstance])
                    return
                }
            }
            ingSystemUserInstance.properties = params
			//ingSystemUserInstance.password = Encrypt.encrypt(ingSystemUserInstance.password)
			ingSystemUserInstance.password = oldPass;
			
            if (!ingSystemUserInstance.hasErrors() && ingSystemUserInstance.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'ingSystemUser.label', default: 'IngSystemUser'), ingSystemUserInstance.id])}"
                redirect(action: "show", id: ingSystemUserInstance.id)
            }
            else {
                render(view: "edit", model: [ingSystemUserInstance: ingSystemUserInstance])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'ingSystemUser.label', default: 'IngSystemUser'), params.id])}"
            redirect(action: "list")
        }
    }

    def delete = {
        def ingSystemUserInstance = IngSystemUser.get(params.id)
        if (ingSystemUserInstance) {
            try {
                ingSystemUserInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'ingSystemUser.label', default: 'IngSystemUser'), params.id])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'ingSystemUser.label', default: 'IngSystemUser'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'ingSystemUser.label', default: 'IngSystemUser'), params.id])}"
            redirect(action: "list")
        }
    }
}
