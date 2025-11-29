/**
 * API Service
 * 
 * Base service for making HTTP requests to the backend.
 * Provides centralized API communication with error handling.
 */
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private readonly baseUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  /**
   * Perform GET request
   */
  get<T>(endpoint: string): Observable<T> {
    return this.http
      .get<T>(`${this.baseUrl}${endpoint}`, { headers: this.getHeaders() })
      .pipe(catchError(this.handleError));
  }

  /**
   * Perform POST request
   */
  post<T>(endpoint: string, body: unknown): Observable<T> {
    return this.http
      .post<T>(`${this.baseUrl}${endpoint}`, body, { headers: this.getHeaders() })
      .pipe(catchError(this.handleError));
  }

  /**
   * Perform PUT request
   */
  put<T>(endpoint: string, body: unknown): Observable<T> {
    return this.http
      .put<T>(`${this.baseUrl}${endpoint}`, body, { headers: this.getHeaders() })
      .pipe(catchError(this.handleError));
  }

  /**
   * Perform DELETE request
   */
  delete<T>(endpoint: string): Observable<T> {
    return this.http
      .delete<T>(`${this.baseUrl}${endpoint}`, { headers: this.getHeaders() })
      .pipe(catchError(this.handleError));
  }

  /**
   * Get common headers for API requests
   */
  private getHeaders(): HttpHeaders {
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    });
  }

  /**
   * Handle HTTP errors
   */
  private handleError(error: HttpErrorResponse): Observable<never> {
    let errorMessage = 'An unexpected error occurred';
    
    if (error.error instanceof ErrorEvent) {
      // Client-side error
      errorMessage = error.error.message;
    } else {
      // Server-side error
      errorMessage = error.error?.message || `Error ${error.status}: ${error.statusText}`;
    }
    
    console.error('API Error:', errorMessage);
    return throwError(() => new Error(errorMessage));
  }
}

