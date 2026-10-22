import { useState, type FormEvent } from 'react';
import BrandHeader from '../auth/BrandHeader';
import { registerStudent } from '../auth/authApi';

interface RegisterStudentProps {
  readonly onBackToLogin: () => void;
  readonly onRegistered: (email: string) => void;
}

export default function RegisterStudent({ onBackToLogin, onRegistered }: RegisterStudentProps) {
  const [fullName, setFullName] = useState('');
  const [rollNumber, setRollNumber] = useState('');
  const [email, setEmail] = useState('');
  const [department, setDepartment] = useState('Computer Science & Engineering');
  const [branch, setBranch] = useState('CSE');
  const [semester, setSemester] = useState<number>(7);
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);

  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  async function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError('');
    setSuccess('');

    if (!email.toLowerCase().endsWith('@institute.ac.in')) {
      setError('Student registration requires an @institute.ac.in institutional email.');
      return;
    }

    if (password.length < 8) {
      setError('Password must be at least 8 characters long.');
      return;
    }

    if (password !== confirmPassword) {
      setError('Password and Confirm Password do not match.');
      return;
    }

    setIsSubmitting(true);
    try {
      const response = await registerStudent({
        fullName: fullName.trim(),
        rollNumber: rollNumber.trim().toUpperCase(),
        email: email.trim().toLowerCase(),
        department,
        branch,
        semester,
        password,
      });

      setSuccess(`${response.message} You can now sign in with your credentials.`);
      setTimeout(() => {
        onRegistered(email.trim().toLowerCase());
      }, 1500);
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : 'Registration failed. Please try again.');
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <main className="auth-page pa-foundation">
      <section className="auth-card auth-card-wide pa-card" aria-labelledby="register-title">
        <BrandHeader />
        <h1 id="register-title">Student Account Claim & Registration</h1>
        <p className="auth-description">
          Register or claim your official student account using your college roll number and institutional email.
        </p>

        {error && <p className="auth-error" role="alert">{error}</p>}
        {success && <p className="auth-success" role="status">{success}</p>}

        <form onSubmit={submit} aria-busy={isSubmitting}>
          <div className="form-grid-2col">
            <div>
              <label htmlFor="reg-fullname">Full Name</label>
              <input
                id="reg-fullname"
                type="text"
                value={fullName}
                onChange={(e) => setFullName(e.target.value)}
                placeholder="Surendra Beniwal"
                required
                disabled={isSubmitting}
              />
            </div>
            <div>
              <label htmlFor="reg-roll">Roll Number</label>
              <input
                id="reg-roll"
                type="text"
                value={rollNumber}
                onChange={(e) => setRollNumber(e.target.value)}
                placeholder="e.g. CS2026-042"
                required
                disabled={isSubmitting}
              />
            </div>
          </div>

          <label htmlFor="reg-email">Institutional Email Address</label>
          <input
            id="reg-email"
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="student.name@institute.ac.in"
            required
            disabled={isSubmitting}
          />

          <div className="form-grid-2col">
            <div>
              <label htmlFor="reg-dept">Department</label>
              <input
                id="reg-dept"
                type="text"
                value={department}
                onChange={(e) => setDepartment(e.target.value)}
                required
                disabled={isSubmitting}
              />
            </div>
            <div>
              <label htmlFor="reg-branch">Branch</label>
              <select
                id="reg-branch"
                value={branch}
                onChange={(e) => setBranch(e.target.value)}
                disabled={isSubmitting}
              >
                <option value="CSE">CSE (Computer Science & Eng)</option>
                <option value="IT">IT (Information Technology)</option>
                <option value="ECE">ECE (Electronics & Comm)</option>
                <option value="MECH">MECH (Mechanical Eng)</option>
                <option value="CIVIL">CIVIL (Civil Eng)</option>
              </select>
            </div>
          </div>

          <div className="form-grid-2col">
            <div>
              <label htmlFor="reg-semester">Current Semester</label>
              <select
                id="reg-semester"
                value={semester}
                onChange={(e) => setSemester(Number(e.target.value))}
                disabled={isSubmitting}
              >
                <option value={1}>Semester 1 (1st Year)</option>
                <option value={2}>Semester 2 (1st Year)</option>
                <option value={3}>Semester 3 (2nd Year)</option>
                <option value={4}>Semester 4 (2nd Year)</option>
                <option value={5}>Semester 5 (3rd Year)</option>
                <option value={6}>Semester 6 (3rd Year)</option>
                <option value={7}>Semester 7 (Final Year)</option>
                <option value={8}>Semester 8 (Final Year)</option>
              </select>
            </div>
            <div>
              <label htmlFor="reg-pass">Create Password (min 8 chars)</label>
              <div className="password-input-wrap">
                <input
                  id="reg-pass"
                  type={showPassword ? 'text' : 'password'}
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder="••••••••••••"
                  required
                  disabled={isSubmitting}
                />
                <button
                  type="button"
                  className="password-toggle-btn"
                  onClick={() => setShowPassword(!showPassword)}
                  aria-label={showPassword ? 'Hide password' : 'Show password'}
                >
                  {showPassword ? 'Hide' : 'Show'}
                </button>
              </div>
            </div>
          </div>

          <div>
            <label htmlFor="reg-confirm-pass">Confirm Password</label>
            <input
              id="reg-confirm-pass"
              type={showPassword ? 'text' : 'password'}
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              placeholder="••••••••••••"
              required
              disabled={isSubmitting}
            />
          </div>

          <button className="auth-submit" type="submit" disabled={isSubmitting}>
            {isSubmitting ? 'Creating account…' : 'Register Student Account'}
          </button>
        </form>

        <button className="auth-link" type="button" onClick={onBackToLogin} disabled={isSubmitting} style={{ marginTop: '16px' }}>
          ← Already have an account? Back to sign in
        </button>
      </section>
    </main>
  );
}
