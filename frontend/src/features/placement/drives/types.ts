export interface PageOf<T> {
  content: T[];
  number: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export type DriveStatus = "DRAFT" | "OPEN" | "CLOSED" | "COMPLETED";
export type EmploymentType = "FULL_TIME" | "INTERNSHIP" | "INTERNSHIP_PPO";
export type OpportunityType = "INTERNSHIP" | "HACKATHON" | "OFF_CAMPUS";

export interface Company {
  id: number;
  name: string;
  industry: string | null;
  website: string | null;
}

export interface CompanyInput {
  name: string;
  industry?: string;
  website?: string;
}

export interface Drive {
  id: number;
  companyId: number;
  companyName: string;
  roleTitle: string;
  employmentType: EmploymentType;
  packageText: string | null;
  ctcLpa: number | null;
  stipendPerMonth: number | null;
  hasPpo: boolean;
  ppoCtcLpa: number | null;
  bondMonths: number | null;
  bondDetails: string | null;
  jobLocation: string | null;
  otherBenefits: string | null;
  venue: string | null;
  reportingTime: string | null;
  instructions: string | null;
  driveDate: string;
  deadline: string;
  status: DriveStatus;
}

export interface Criteria {
  driveId: number;
  minCgpa: number | null;
  maxBacklogs: number | null;
  minClass10: number | null;
  minClass12: number | null;
  minAttendance: number | null;
  allowedBranches: string | null;
  passingYear: number | null;
  minAtsScore: number | null;
}

export interface Talk {
  id: number;
  title: string;
  speakerName: string | null;
  companyId: number | null;
  talkAt: string;
  venueOrLink: string | null;
  description: string | null;
  audience: string;
}

export interface Opportunity {
  id: number;
  type: OpportunityType;
  title: string;
  organisation: string;
  description: string | null;
  link: string | null;
  location: string | null;
  stipendOrPackage: string | null;
  startDate: string | null;
  endDate: string | null;
  deadline: string | null;
  finalYearOnly: boolean;
}
