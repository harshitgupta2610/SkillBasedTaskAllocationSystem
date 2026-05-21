import { Component } from '@angular/core';
import { ThemeService } from './services/theme.service';

@Component({
  selector: 'app-root',
  template: '<router-outlet></router-outlet>'
})
export class AppComponent {
  // Injecting ThemeService here ensures it boots early — its constructor
  // restores the saved theme from localStorage before any page renders.
  constructor(private themeService: ThemeService) {}
}
