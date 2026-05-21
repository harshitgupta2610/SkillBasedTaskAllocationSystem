import { Component, OnInit } from '@angular/core';
import { Location } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { UserService } from '../../services/user.service';
import { SkillService } from '../../services/skill.service';
import { ThemeService } from '../../services/theme.service';
import { User, SkillSummary } from '../../models/user.model';
import { Skill } from '../../models/skill.model';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html'
})
export class ProfileComponent implements OnInit {

  user!: User;
  skills: Skill[] = [];
  loading = true;
  saving  = false;

  // Edit name
  editingName = false;
  nameInput   = '';
  nameError   = '';

  // Add skill form
  showAddSkill   = false;
  newSkillId     = 0;
  newProficiency = 3;
  newYears       = 0;
  skillError     = '';
  addingSkill    = false;

  // Edit skill
  editingSkillId: number | null = null;
  editProficiency = 3;
  editYears       = 0;

  // Removing
  removingSkillId: number | null = null;

  readonly proficiencies = [
    { value: 1, label: 'Beginner' },
    { value: 2, label: 'Elementary' },
    { value: 3, label: 'Intermediate' },
    { value: 4, label: 'Advanced' },
    { value: 5, label: 'Expert' }
  ];

  constructor(
    public authService: AuthService,
    public themeService: ThemeService,
    private userService: UserService,
    private skillService: SkillService,
    public location: Location
  ) {}

  ngOnInit(): void {
    this.userService.getMe().subscribe(u => { this.user = u; this.loading = false; });
    this.skillService.getAll().subscribe(s => this.skills = s);
  }

  // ── Name ──────────────────────────────────────────────────────
  startEditName(): void {
    this.nameInput   = this.user.name;
    this.editingName = true;
    this.nameError   = '';
  }

  saveName(): void {
    if (!this.nameInput.trim()) { this.nameError = 'Name cannot be empty'; return; }
    this.saving = true;
    this.userService.updateProfile(this.user.id, this.nameInput.trim()).subscribe({
      next: u => { this.user = u; this.editingName = false; this.saving = false; },
      error: () => { this.nameError = 'Failed to update name'; this.saving = false; }
    });
  }

  // ── Availability ──────────────────────────────────────────────
  toggleAvailability(): void {
    this.userService.updateAvailability(this.user.id, !this.user.available).subscribe(u => this.user = u);
  }

  // ── Add skill ─────────────────────────────────────────────────
  openAddSkill(): void {
    this.showAddSkill   = true;
    this.newSkillId     = 0;
    this.newProficiency = 3;
    this.newYears       = 0;
    this.skillError     = '';
  }

  addSkill(): void {
    if (!this.newSkillId) { this.skillError = 'Please select a skill'; return; }
    this.skillError  = '';
    this.addingSkill = true;
    this.userService.addSkill(this.user.id, this.newSkillId, this.newProficiency, this.newYears).subscribe({
      next: u => { this.user = u; this.showAddSkill = false; this.addingSkill = false; },
      error: err => {
        this.skillError  = err?.error?.message ?? 'Skill already added or not found';
        this.addingSkill = false;
      }
    });
  }

  // ── Edit skill ────────────────────────────────────────────────
  startEditSkill(s: SkillSummary): void {
    this.editingSkillId = s.userSkillId;
    this.editProficiency = s.proficiencyLevel;
    this.editYears       = s.yearsExperience;
  }

  saveSkill(s: SkillSummary): void {
    this.userService.updateSkill(this.user.id, s.userSkillId, this.editProficiency, this.editYears).subscribe({
      next: u => { this.user = u; this.editingSkillId = null; },
      error: () => {}
    });
  }

  // ── Remove skill ──────────────────────────────────────────────
  removeSkill(s: SkillSummary): void {
    this.removingSkillId = s.userSkillId;
    this.userService.removeSkill(this.user.id, s.userSkillId).subscribe({
      next: u => { this.user = u; this.removingSkillId = null; },
      error: () => { this.removingSkillId = null; }
    });
  }

  // ── Helpers ───────────────────────────────────────────────────
  profLabel(level: number): string {
    return this.proficiencies.find(p => p.value === level)?.label ?? String(level);
  }

  profColor(level: number): string {
    return ['', '#94a3b8','#60a5fa','#34d399','#f59e0b','#8b5cf6'][level] ?? '#94a3b8';
  }

  get availableSkillsToAdd(): Skill[] {
    const already = new Set(this.user?.skills.map(s => s.skillId) ?? []);
    return this.skills.filter(s => !already.has(s.id));
  }

  get skillsByCategory(): { category: string; skills: SkillSummary[] }[] {
    const map = new Map<string, SkillSummary[]>();
    for (const s of (this.user?.skills ?? [])) {
      const cat = s.category || 'Other';
      if (!map.has(cat)) map.set(cat, []);
      map.get(cat)!.push(s);
    }
    return Array.from(map.entries()).map(([category, skills]) => ({ category, skills }));
  }

  get skillCategories(): string[] {
    const cats = new Set(this.skills.map(s => s.category || 'Other'));
    return Array.from(cats).sort();
  }

  skillsByGroup(category: string): Skill[] {
    const already = new Set(this.user?.skills.map(s => s.skillId) ?? []);
    return this.skills.filter(s => (s.category || 'Other') === category && !already.has(s.id));
  }
}
