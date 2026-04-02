import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { SidebarComponent } from '../sidebar/sidebar';
import { NavbarComponent } from '../navbar/navbar';
import { NotificationToastComponent } from '../../notifications/toast/notification-toast';

@Component({
  selector: 'fp-shell',
  standalone: true,
  imports: [RouterOutlet, SidebarComponent, NavbarComponent, NotificationToastComponent],
  templateUrl: './shell.html',
})
export class ShellComponent {}
