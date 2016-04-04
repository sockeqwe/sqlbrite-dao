package com.hannesdorfmann.sqlbritedao.sample.model.customer

import com.hannesdorfmann.sqlbrite.objectmapper.annotation.Column
import com.hannesdorfmann.sqlbrite.objectmapper.annotation.ObjectMappable

/**
 *
 *
 * @author Hannes Dorfmann
 */
@ObjectMappable
class KotlinCustomer() {

    @Column("street")
    var street: String? = null


    @Column("isFoo")
    var foo : Boolean = true

    @Column("asd")
    var isValid =false

  fun getStreet(){

  }


}