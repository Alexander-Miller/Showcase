import {Component} from "@angular/core";
import {TranslateService} from "ng2-translate/ng2-translate";

@Component({
    selector: "educama-app",
    templateUrl: "./app.component.html"
})
export class AppComponent {

    constructor(private _translateService: TranslateService) {
        this.configureLanguage();
    }

    private configureLanguage() {
        // configure language for translation service
        let userLang = navigator.language.split("-")[0];
        userLang = /(de|en)/gi.test(userLang) ? userLang : "en";
        this._translateService.setDefaultLang("en");
        this._translateService.use(userLang);
    }

}
