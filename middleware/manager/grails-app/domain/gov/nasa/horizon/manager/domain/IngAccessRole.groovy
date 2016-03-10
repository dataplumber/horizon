/*
 * Copyright (c) 2008-2013 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 */
package gov.nasa.horizon.manager.domain

/**
 * Definition of Access Role domain class
 *
 * @author T. Huang [Thomas.Huang@jpl.nasa.gov]
 * @version $Id: IngAccessRole.groovy 2173 2008-10-29 20:06:48Z thuang $
 */
class IngAccessRole {

   /**
    * The access role name
    */
   String name

   /**
    * A bit mask to describe various access roles
    *    0: no capabilility
    *    1: get, list, subscribe, notify (g)
    *    2: add (a)
    *    4: replace (r)
    *    8: delete (d)
    *   16: offline (o)
    *   32: rename (n)
    *   64: notify (t)
    *  128: confirm (c)
    *  256: qaAccess (q)
    *  512: archive
    * 1024: subtype
    * 2048: receipt
    * 4096: (l)ock product type
    */
   Integer capabilities

   static constraints = {
      name(unique: true, size: 1..30)
      capabilities(nullable: false, min: 0)
   }

   static mapping = {
      id generator: 'sequence', params: [sequence: 'ing_accessrole_id_seq']
   }
}
