package de.bagra.carwebcrawler.vaadin.component.validator

import com.vaadin.flow.data.binder.ValidationResult
import com.vaadin.flow.data.binder.ValidationResult.error
import com.vaadin.flow.data.binder.ValidationResult.ok
import com.vaadin.flow.data.binder.Validator
import com.vaadin.flow.data.binder.ValueContext
import de.bagra.carwebcrawler.vaadin.model.FromToPriceModel

class PriceFromToValidator(private val min: Double, private val max: Double?): Validator<FromToPriceModel> {
    override fun apply(fromToPriceModel: FromToPriceModel?, context: ValueContext?): ValidationResult {
        if (fromToPriceModel != null) {
            if (fromToPriceModel.fromValue != null && fromToPriceModel.toValue != null 
                && fromToPriceModel.fromValue!! > fromToPriceModel.toValue!!
            ) {
                return error("'Preis bis' sollte größer als 'Preis von' sein")
            }
            if ( fromToPriceModel.fromValue != null && fromToPriceModel.fromValue!! < min) {
                return error("'Preis von' sollte größer als 0 sein")
            }
            if (max != null && fromToPriceModel.toValue != null && fromToPriceModel.toValue!! > max) {
                return error("'Preis von' sollte nicht größer als $max sein")
            }
        }
        return ok()
    }
}