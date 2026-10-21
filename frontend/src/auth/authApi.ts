export type Portal = 'INSTITUTE' | 'PLACEMENT';

export interface AuthenticatedSession {
  readonly accessToken: string;
  readonly tokenType: 'Bearer';
  readonly expiresIn: number;
  readonly email: string;
  readonly roles: readonly string[];
}

interface ApiError {
  readonly code?: string;
  readonly message?: string;
}

function isAuthenticatedSession(value: unknown): value is AuthenticatedSession {
  if (typeof value !== 'object' || value === null) return false;
  const session = value as Record<string, unknown>;
  return typeof session.accessToken === 'string'
    && session.accessToken.length > 0
    && session.tokenType === 'Bearer'
    && typeof session.expiresIn === 'number'
    && session.expiresIn > 0
    && typeof session.email === 'string'
    && Array.isArray(session.roles)
    && session.roles.every((role) => typeof role === 'string');
}

function getErrorMessage(value: unknown, status: number): string {
  if (typeof value === 'object' && value !== null) {
    const error = value as ApiError;
    if (error.code === 'AUTHENTICATION_FAILED') return 'Invalid email or password.';
    if (typeof error.message === 'string' && error.message.length > 0) return error.message;
  }
  if (status === 403) return 'This account cannot access the selected portal.';
  return 'Unable to sign in. Please try again.';
}

export async function login(
  portal: Portal,
  email: string,
  password: string,
): Promise<AuthenticatedSession> {
  let response: Response;
  try {
    const apiBaseUrl = (import.meta.env.VITE_API_BASE_URL ?? '').replace(/\/+$/, '');
    response = await fetch(`${apiBaseUrl}/api/v1/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ portal, email, password }),
    });
  } catch {
    throw new Error('Unable to reach the sign-in service. Check your connection and try again.');
  }

  let body: unknown;
  try {
    body = await response.json();
  } catch {
    throw new Error('The sign-in service returned an unreadable response.');
  }

  if (!response.ok) {
    throw new Error(getErrorMessage(body, response.status));
  }
  if (!isAuthenticatedSession(body)) {
    throw new Error('The sign-in service returned an invalid response.');
  }
  return body;
}
