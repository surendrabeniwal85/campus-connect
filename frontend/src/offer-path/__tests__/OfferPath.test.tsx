import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import OfferPathStudentView from '../OfferPathStudentView';
import OfferPathTpoConsole from '../OfferPathTpoConsole';
import * as authApi from '../../auth/authApi';

// Mock authApi functions
vi.mock('../../auth/authApi', async () => {
  const actual = await vi.importActual<typeof authApi>('../../auth/authApi');
  return {
    ...actual,
    fetchUpcomingDrives: vi.fn().mockResolvedValue({
      content: [
        {
          id: 1,
          companyName: 'Demo Tech Pvt Ltd',
          roleTitle: 'Graduate Software Engineer Trainee',
          ctcLpa: 8.5,
          jobLocation: 'Bengaluru / Pune',
          driveDate: '2026-11-20',
          minCgpa: 7.5,
          maxBacklogs: 0,
          minCrtAttendance: 75.0,
          eligibleBranches: ['CSE', 'IT'],
          hasPpo: false,
        },
      ],
      totalElements: 1,
    }),
    fetchDriveEligibility: vi.fn().mockResolvedValue({
      driveId: 1,
      eligible: true,
      hasMissingData: false,
      rules: [
        {
          rule: 'Minimum CGPA Cutoff',
          expected: '>= 7.50',
          actual: '8.52',
          status: 'PASS',
          nextAction: 'CGPA criteria fulfilled.',
        },
      ],
    }),
    simulateOfferPath: vi.fn().mockResolvedValue({
      actualResults: [],
      simulatedResults: [],
      newlyUnlockedDrives: [
        {
          driveId: 3,
          companyName: 'Pretend Systems',
          roleTitle: 'Cloud Systems Associate',
          changedRules: [
            {
              rule: 'Minimum CGPA Cutoff',
              fromStatus: 'FAIL',
              toStatus: 'PASS',
              reason: 'Simulated CGPA 8.90 satisfies cutoff 8.80',
            },
          ],
        },
      ],
    }),
    fetchOneRuleAwaySummary: vi.fn().mockResolvedValue({
      driveId: 1,
      companyName: 'Demo Tech Pvt Ltd',
      roleTitle: 'Graduate Software Engineer Trainee',
      totalAssessed: 4,
      eligibleCount: 2,
      oneRuleAwayCount: 2,
      missingDataCount: 0,
      unmetRuleBreakdown: {
        'Minimum CGPA Cutoff': 1,
        'Active Backlog Clearance': 1,
      },
      candidates: [
        {
          studentId: 101,
          fullName: 'Amit Kumar',
          rollNumber: 'CS2026-088',
          email: 'amit@institute.ac.in',
          unmetRule: 'Minimum CGPA Cutoff',
          actualValue: '7.20',
          requiredValue: '>= 7.50',
          nextAction: 'Raise semester GPA above 7.50',
        },
      ],
    }),
    sendTargetedNudge: vi.fn().mockResolvedValue({
      driveId: 1,
      driveTitle: 'Demo Tech Pvt Ltd - Graduate Software Engineer Trainee',
      targetedCount: 1,
      sentCount: 1,
      status: 'SENT',
      message: 'Targeted preparation alert dispatched successfully.',
    }),
  };
});

describe('OfferPathStudentView', () => {
  const dummyProfile = {
    id: 1,
    userId: 1,
    rollNumber: 'CS2026-001',
    fullName: 'Surendra Beniwal',
    email: 'student@institute.ac.in',
    course: 'B.Tech',
    department: 'Computer Science & Engineering',
    branch: 'CSE',
    semester: 7,
    cgpa: 8.52,
    class10Percent: 88.5,
    class12Percent: 86.0,
    activeBacklogs: 0,
    passingYear: 2027,
    atsScore: 82,
    finalYear: true,
  };

  it('renders Offer Path banner, product promise, and journey rail', () => {
    render(<OfferPathStudentView token="dummy-token" studentProfile={dummyProfile} />);

    expect(screen.getByText(/Student Offer Path & Eligibility Engine/i)).toBeInTheDocument();
    expect(screen.getByText(/what a student needs to do next/i)).toBeInTheDocument();
    expect(screen.getByText(/1. Profile Verified/i)).toBeInTheDocument();
    expect(screen.getByText(/4. Recruiter Engine Match/i)).toBeInTheDocument();
  });

  it('renders What-If Simulation sliders with integrity disclaimer', () => {
    render(<OfferPathStudentView token="dummy-token" studentProfile={dummyProfile} />);

    expect(screen.getByText(/What-If Simulator/i)).toBeInTheDocument();
    expect(screen.getByText(/Simulation Disclaimer/i)).toBeInTheDocument();

    const cgpaSlider = screen.getByLabelText(/Simulated CGPA/i);
    expect(cgpaSlider).toBeInTheDocument();

    fireEvent.change(cgpaSlider, { target: { value: '8.90' } });
    expect(screen.getByText(/8.90/i)).toBeInTheDocument();
  });
});

describe('OfferPathTpoConsole', () => {
  it('renders TPO metrics, unmet breakdown, and one-rule-away candidate table', async () => {
    render(<OfferPathTpoConsole token="dummy-token" />);

    expect(screen.getByText(/Offer Path: One-Rule-Away Cohort Analytics/i)).toBeInTheDocument();
    expect(screen.getByText(/Total Cohort Assessed/i)).toBeInTheDocument();
    expect(screen.getByText(/Currently Eligible/i)).toBeInTheDocument();
    expect(screen.getByText(/Exactly One Rule Away/i)).toBeInTheDocument();
    expect(screen.getByText(/Missing Required Data/i)).toBeInTheDocument();

    // Candidate should appear after data fetch
    const candidateName = await screen.findByText('Amit Kumar');
    expect(candidateName).toBeInTheDocument();
    expect(screen.getByText('CS2026-088')).toBeInTheDocument();
  });

  it('opens targeted nudge modal and confirms dispatch', async () => {
    render(<OfferPathTpoConsole token="dummy-token" />);

    const candidate = await screen.findByText('Amit Kumar');
    expect(candidate).toBeInTheDocument();

    const nudgeBtn = screen.getByText(/Dispatch Targeted Nudge/i);
    fireEvent.click(nudgeBtn);

    expect(screen.getByText(/Confirm Preparation Alert/i)).toBeInTheDocument();

    const checkbox = screen.getByRole('checkbox', { name: /I confirm dispatching this targeted preparation alert/i });
    expect(checkbox).toBeInTheDocument();
    fireEvent.click(checkbox);

    const dispatchBtn = screen.getByRole('button', { name: /Send Targeted Nudge Now/i });
    expect(dispatchBtn).not.toBeDisabled();
    fireEvent.click(dispatchBtn);

    const successMsg = await screen.findByText(/Targeted Nudge Dispatched Successfully!/i);
    expect(successMsg).toBeInTheDocument();
  });
});
