import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { TaskService } from '../../services/task.service';
import { UserService } from '../../services/user.service';
import { SkillService } from '../../services/skill.service';
import { ThemeService } from '../../services/theme.service';
import { Task, TaskCreateRequest, TaskStatus, Priority } from '../../models/task.model';
import { User } from '../../models/user.model';
import { Skill } from '../../models/skill.model';

@Component({
  selector: 'app-manager',
  templateUrl: './manager.component.html'
})
export class ManagerComponent implements OnInit {
  activeTab = 'tasks';

  // Tasks
  tasks: Task[] = [];
  tasksLoading = false;

  // Employees
  employees: User[] = [];
  employeesLoading = false;

  // Skills
  skills: Skill[] = [];

  // Create task form
  showCreateForm = false;
  createLoading  = false;
  createError    = '';
  lastCreated: Task | null = null;

  newTask: TaskCreateRequest = {
    title: '', description: '', priority: 'MEDIUM', deadline: '',
    requiredSkills: []
  };

  // Assign task modal state
  assignTaskId: number | null = null;
  assignEmployeeId: number | null = null;

  // Filters
  statusFilter = '';
  priorityFilter = '';

  constructor(
    public authService: AuthService,
    public themeService: ThemeService,
    private taskService: TaskService,
    private userService: UserService,
    private skillService: SkillService
  ) {}

  ngOnInit(): void {
    this.loadTasks();
    this.loadEmployees();
    this.skillService.getAll().subscribe(s => this.skills = s);
  }

  loadTasks(): void {
    this.tasksLoading = true;
    this.taskService.getAllTasks().subscribe({
      next: t => { this.tasks = t; this.tasksLoading = false; },
      error: () => this.tasksLoading = false
    });
  }

  loadEmployees(): void {
    this.employeesLoading = true;
    this.userService.getAllEmployees().subscribe({
      next: e => { this.employees = e; this.employeesLoading = false; },
      error: () => this.employeesLoading = false
    });
  }

  // ── Create Task ───────────────────────────
  addSkillReq(): void {
    this.newTask.requiredSkills.push({ skillId: 0, minProficiencyLevel: 1 });
  }

  removeSkillReq(i: number): void {
    this.newTask.requiredSkills.splice(i, 1);
  }

  submitTask(): void {
    if (!this.newTask.title.trim()) { this.createError = 'Title is required.'; return; }
    this.createError  = '';
    this.createLoading = true;
    this.lastCreated   = null;

    const payload = {
      ...this.newTask,
      requiredSkills: this.newTask.requiredSkills.filter(s => s.skillId > 0)
    };

    this.taskService.createTask(payload).subscribe({
      next: task => {
        this.createLoading = false;
        this.lastCreated   = task;
        this.showCreateForm = false;
        this.resetForm();
        this.loadTasks();
      },
      error: () => {
        this.createLoading = false;
        this.createError   = 'Failed to create task. Please try again.';
      }
    });
  }

  resetForm(): void {
    this.newTask = { title: '', description: '', priority: 'MEDIUM', deadline: '', requiredSkills: [] };
  }

  // ── Manual Assign ────────────────────────
  doManualAssign(): void {
    if (!this.assignTaskId || !this.assignEmployeeId) return;
    this.taskService.manualAssign(this.assignTaskId, this.assignEmployeeId).subscribe({
      next: () => { this.assignTaskId = null; this.assignEmployeeId = null; this.loadTasks(); }
    });
  }

  openAssign(taskId: number): void {
    this.assignTaskId = taskId;
    this.assignEmployeeId = null;
  }

  // ── Helpers ──────────────────────────────
  get filteredTasks(): Task[] {
    return this.tasks.filter(t =>
      (!this.statusFilter   || t.status   === this.statusFilter) &&
      (!this.priorityFilter || t.priority === this.priorityFilter)
    );
  }

  get stats() {
    return {
      total:      this.tasks.length,
      open:       this.tasks.filter(t => t.status === 'OPEN').length,
      inProgress: this.tasks.filter(t => t.status === 'IN_PROGRESS').length,
      done:       this.tasks.filter(t => t.status === 'DONE').length,
      assigned:   this.tasks.filter(t => t.status === 'ASSIGNED').length,
    };
  }

  proficiencyLabel(level: number): string {
    return ['', 'Beginner', 'Elementary', 'Intermediate', 'Advanced', 'Expert'][level] ?? String(level);
  }

  priorities: Priority[] = ['LOW', 'MEDIUM', 'HIGH', 'CRITICAL'];
  proficiencies = [1, 2, 3, 4, 5];
}
