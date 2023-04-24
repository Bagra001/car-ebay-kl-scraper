package de.bagra.carwebcrawler.views.main

import com.vaadin.flow.component.Text
import com.vaadin.flow.component.Unit
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.html.Hr
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.notification.NotificationVariant
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.progressbar.ProgressBar
import com.vaadin.flow.component.textfield.NumberField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.data.validator.RangeValidator
import com.vaadin.flow.router.Route
import de.bagra.carwebcrawler.service.CrawlerService
import de.bagra.carwebcrawler.util.LogUtil
import de.bagra.carwebcrawler.vaadin.component.PriceFromToField
import de.bagra.carwebcrawler.vaadin.component.validator.PriceFromToValidator
import de.bagra.carwebcrawler.vaadin.model.CrawlerDataFilter
import java.time.LocalDate
import java.time.ZoneId
import java.util.*
import java.util.concurrent.CancellationException
import java.util.stream.Collectors
import java.util.stream.IntStream

//TODO Grid hinzufügen
@Route
class MainView(crawlerService: CrawlerService, logUtil: LogUtil) : VerticalLayout() {

    private var crawlerService: CrawlerService
    private var logUtil: LogUtil

    private var binder = Binder(CrawlerDataFilter::class.java)

    private lateinit var priceFromToField: PriceFromToField
    private lateinit var modelTextField: TextField
    private lateinit var erstZulassungDatePicker: ComboBox<Any?>
    private lateinit var psNumberField: NumberField
    private lateinit var stopCrawelButton: Button
    private lateinit var crawlButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var progressBarLayout: VerticalLayout
    
    private var crawlerData: CrawlerDataFilter? = CrawlerDataFilter()
    
    init {
        this.crawlerService = crawlerService
        this.logUtil = logUtil
        add(
            progressBar(),
            H1("Crawler Properties"),
            createFirstLine(),
            HorizontalLayout(crawlButton(), stopCrawlerButton(), loadCrawledDataButton()),
            Hr()
        )
        initBinder()
    }

    private fun progressBar(): VerticalLayout {
        progressBar = ProgressBar()
        progressBar.isIndeterminate = true
        progressBar.isVisible = false

        val progressBarLabel = Div()
        progressBarLabel.text = "Crawling..."

        progressBarLayout = VerticalLayout(progressBarLabel, progressBar)
        progressBarLayout.isVisible = false
        return progressBarLayout
    }
    
    private fun createFirstLine(): HorizontalLayout {
        modelTextField = TextField("Marke/Model:")
        modelTextField.isRequired = true
        modelTextField.isClearButtonVisible = true

        erstZulassungDatePicker = ComboBox<Any?>("Erstzulassung:", eZYears())
        erstZulassungDatePicker.setWidth(10F, Unit.EM)

        psNumberField = NumberField("mindestens Leistung:")
        psNumberField.suffixComponent = psSuffix()
        psNumberField.isClearButtonVisible = true
        
        priceFromToField = PriceFromToField("Preis von:", "Preis bis:")
        priceFromToField.addValueChangeListener { binder.validate() }
        
        return HorizontalLayout(modelTextField, erstZulassungDatePicker, priceFromToField, psNumberField)
    }
    
    private fun crawlButton(): Button {
        crawlButton = Button("Starte das Crawlen")
        crawlButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS)
        crawlButton.icon = Icon(VaadinIcon.PLAY)
        crawlButton.isIconAfterText = true
        crawlButton.addClickListener {
            startCrawlingState()
        }
        return crawlButton
    }

    private fun stopCrawlerButton(): Button {
        stopCrawelButton = Button(Icon(VaadinIcon.STOP))
        stopCrawelButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR)
        stopCrawelButton.addClickListener {
            stopCrawlingState()
        }
        stopCrawelButton.isEnabled = false
        return stopCrawelButton
    }

    private fun loadCrawledDataButton(): Button {
        val loadCrawledDataButton = Button()
        loadCrawledDataButton.setTooltipText("Lade alle zuletzt gecrawlten Daten") 
        loadCrawledDataButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ICON)
        loadCrawledDataButton.icon = Icon(VaadinIcon.DOWNLOAD)
        loadCrawledDataButton.isIconAfterText = true
        return loadCrawledDataButton
    }
    
    private fun psSuffix(): Div {
        val psSuffix = Div()
        psSuffix.text = "Ps"
        return psSuffix
    }
    
    private fun eZYears(): List<Int> {
        val now: LocalDate = LocalDate.now(ZoneId.systemDefault())
        return IntStream
            .range(now.year - 30, now.year + 1).boxed().sorted(Collections.reverseOrder())
            .collect(Collectors.toList())
    }

    //TODO bei Fehler Error anzeigen
    private fun startCrawlingState() {
        binder.writeBeanIfValid(crawlerData);
        crawlerService.startCrawling()
        stopCrawelButton.isEnabled = true
        progressBar.isVisible = true
        crawlButton.isEnabled = false
        progressBarLayout.isVisible = true
    }

    private fun stopCrawlingState() {
        try {
            crawlerService.stopForceCrawling()
        } catch (ex: CancellationException) {
            crawlerStopped()
        } finally {
            crawlerStopped()
        }
    }

    private fun crawlerStopped() {
        if (progressBar.isVisible) {
            progressBar.isVisible = false
            crawlButton.isEnabled = true
            stopCrawelButton.isEnabled = false
            progressBarLayout.isVisible = false
            resultNotification()
        }
    }

    private fun resultNotification() {
        val notification = Notification()
        notification.position = Notification.Position.MIDDLE
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS)

        val text = Div(Text(crawlerService.getResult()))

        val closeButton = Button(Icon("lumo", "cross"))
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE)
        closeButton.element.setAttribute("aria-label", "Close")
        closeButton.addClickListener { notification.close() }

        val layout = HorizontalLayout(text, closeButton)
        layout.alignItems = FlexComponent.Alignment.CENTER

        notification.add(layout)
        notification.open()
    }

    private fun errorNotification(msg: String) {
        val notification = Notification()
        notification.position = Notification.Position.MIDDLE
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR)

        val text = Div(Text(msg))

        val closeButton = Button(Icon("lumo", "cross"))
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE)
        closeButton.element.setAttribute("aria-label", "Close")
        closeButton.addClickListener { notification.close() }

        val layout = HorizontalLayout(text, closeButton)
        layout.alignItems = FlexComponent.Alignment.CENTER

        notification.add(layout)
        notification.open()
    }

    private fun initBinder() {
        binder.forField(priceFromToField).withValidator(PriceFromToValidator(0.0, null)).bind("fromToPriceModel");
        binder.forField(modelTextField).bind("model")
        binder.forField(psNumberField).withValidator(RangeValidator.of("Keinen negativen Zahlen",0.0, 999999.0)).bind("ps")
        binder.forField(erstZulassungDatePicker).bind("ez")
    }
}