import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { TaskService } from '../../services/task.service';
import { UserService } from '../../services/user.service';
import { ThemeService } from '../../services/theme.service';
import { Task, TaskStatus } from '../../models/task.model';

@Component({
  selector: 'app-employee',
  templateUrl: './employee.component.html'
})
export class EmployeeComponent implements OnInit {
  tasks: Task[] = [];
  loading = false;
  notifications: any[] = [];
  unreadCount = 0;
  showNotifications = false;
  updatingTaskId: number | null = null;

  constructor(
    public authService: AuthService,
    public themeService: ThemeService,
    private taskService: TaskService,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    this.loadTasks();
    this.loadNotifications();
  }

  loadTasks(): void {
    this.loading = true;
    this.taskService.getMyTasks().subscribe({
      next: t => { this.tasks = t; this.loading = false; },
      error: () => this.loading = false
    });
  }

  loadNotifications(): void {
    const uid = this.authService.currentUser?.userId;
    if (!uid) return;
    this.userService.getNotifications(uid).subscribe(n => {
      this.notifications = n;
      this.unreadCount   = n.filter((x: any) => !x.read).length;
    });
  }

  markAllRead(): void {
    const uid = this.authService.currentUser?.userId;
    if (!uid) return;
    this.userService.markAllRead(uid).subscribe(() => {
      this.notifications.forEach(n => n.read = true);
      this.unreadCount = 0;
    });
  }

  updateStatus(task: Task, status: TaskStatus): void {
    this.updatingTaskId = task.id;
    this.taskService.updateStatus(task.id, status).subscribe({
      next: updated => {
        const idx = this.tasks.findIndex(t => t.id === updated.id);
        if (idx !== -1) this.tasks[idx] = updated;
        this.updatingTaskId = null;
      },
      error: () => this.updatingTaskId = null
    });
  }

  nextStatus(current: TaskStatus): TaskStatus | null {
    const flow: Partial<Record<TaskStatus, TaskStatus>> = {
      ASSIGNED: 'IN_PROGRESS',
      IN_PROGRESS: 'DONE'
    };
    return flow[current] ?? null;
  }

  nextStatusLabel(current: TaskStatus): string {
    const next = this.nextStatus(current);
    return next === 'IN_PROGRESS' ? 'Start Working' : next === 'DONE' ? 'Mark Done' : '';
  }

  get stats() {
    return {
      total:      this.tasks.length,
      assigned:   this.tasks.filter(t => t.status === 'ASSIGNED').length,
      inProgress: this.tasks.filter(t => t.status === 'IN_PROGRESS').length,
      done:       this.tasks.filter(t => t.status === 'DONE').length,
    };
  }

  proficiencyLabel(level: number): string {
    return ['', 'Beginner', 'Elementary', 'Intermediate', 'Advanced', 'Expert'][level] ?? String(level);
  }

  priorityIcon(p: string): string {
    return { LOW: 'bi-arrow-down', MEDIUM: 'bi-dash', HIGH: 'bi-arrow-up', CRITICAL: 'bi-exclamation-triangle-fill' }[p] ?? '';
  }
}
