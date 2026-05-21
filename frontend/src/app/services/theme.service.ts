import { Injectable } from '@angular/core';

/**
 * Manages light/dark theme.
 * - Toggles a `data-theme="dark"` attribute on <html>
 * - Persists choice in localStorage so refresh keeps the theme
 * - Components can read `isDark` for icon switching
 */
@Injectable({ providedIn: 'root' })
export class ThemeService {
  private readonly KEY = 'skilltask_theme';
  private _isDark = false;

  constructor() {
    this.restore();
  }

  get isDark(): boolean {
    return this._isDark;
  }

  toggle(): void {
    this.apply(!this._isDark);
  }

  setDark(dark: boolean): void {
    this.apply(dark);
  }

  private restore(): void {
    const saved = localStorage.getItem(this.KEY);
    this.apply(saved === 'dark');
  }

  private apply(dark: boolean): void {
    this._isDark = dark;
    document.documentElement.setAttribute('data-theme', dark ? 'dark' : 'light');
    localStorage.setItem(this.KEY, dark ? 'dark' : 'light');
  }
}
