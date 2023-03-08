package de.bagra.carwebcrawler.vaadin.component

import com.vaadin.flow.component.customfield.CustomField
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.textfield.NumberField
import de.bagra.carwebcrawler.vaadin.model.FromToPriceModel


class PriceFromToField(fromText: String, toText: String): CustomField<FromToPriceModel>() {

    private var priceFromNumberField: NumberField = NumberField(fromText)
    private var priceToNumberField: NumberField = NumberField(toText)
    private var fromToPriceModel: FromToPriceModel = FromToPriceModel(null, null)

    init {
        priceFromNumberField.isClearButtonVisible = true
        priceToNumberField.isClearButtonVisible = true
        
        priceFromNumberField.suffixComponent = euroSuffix()
        priceToNumberField.suffixComponent = euroSuffix()

        priceFromNumberField.addValueChangeListener { fromValue -> fromToPriceModel.fromValue = fromValue.value }
        priceToNumberField.addValueChangeListener { toValue ->
            run {
                fromToPriceModel.toValue = toValue.value
                priceFromNumberField.value = 0.0
            }
        }
        
        add(priceFromNumberField, Label(" - "), priceToNumberField)
    }

    private fun euroSuffix(): Div {
        val euroSuffix = Div()
        euroSuffix.text = "€"
        return euroSuffix
    }
    
    override fun setPresentationValue(newPresentationValue: FromToPriceModel?) {
        if (newPresentationValue != null) {
            priceFromNumberField.value = newPresentationValue.fromValue
        }
        if (newPresentationValue != null) {
            priceToNumberField.value = newPresentationValue.toValue
        }
    }

    override fun generateModelValue(): FromToPriceModel {
        return FromToPriceModel(this.priceFromNumberField.value, this.priceToNumberField.value)
    }

    override fun getValue(): FromToPriceModel {
        return fromToPriceModel
    }

    override fun setValue(value: FromToPriceModel?) {
        super.setValue(value)
    }
}