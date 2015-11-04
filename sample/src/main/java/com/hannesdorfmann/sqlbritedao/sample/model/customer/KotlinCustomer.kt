package com.hannesdorfmann.sqlbritedao.sample.model.customer

import com.hannesdorfmann.sqlbrite.objectmapper.annotation.Column
import com.hannesdorfmann.sqlbrite.objectmapper.annotation.ObjectMappable
import com.hannesdorfmann.sqlbritedao.sample.model.customer.Customer

/**
 *
 *
 * @author Hannes Dorfmann
 */
@ObjectMappable
class KotlinCustomer : Customer() {

    @Column("street")
    var street: String? = null
}