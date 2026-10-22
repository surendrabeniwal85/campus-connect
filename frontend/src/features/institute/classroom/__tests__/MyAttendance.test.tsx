import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { beforeEach, describe, expect, it, vi } from "vitest";
import MyAttendance from "../MyAttendance";

vi.mock("../classroomApi", () => ({
  classroomApi: {
    attendanceSummary: vi.fn().mockResolvedValue({
      totalSessions: 10, present: 7, absent: 2, late: 1, percentage: 75,
      shortage: false, studentName: "Demo Student",
    }),
  },
}));

describe("MyAttendance", () => {
  beforeEach(() => vi.clearAllMocks());
  it("validates the student id", async () => {
    render(<MyAttendance />);
    fireEvent.click(screen.getByRole("button", { name: /view summary/i }));
    expect(await screen.findByRole("alert")).toHaveTextContent(/valid student id/i);
  });
  it("renders attendance summary after successful lookup", async () => {
    render(<MyAttendance />);
    fireEvent.change(screen.getByLabelText(/student id/i), { target: { value: "12" } });
    fireEvent.click(screen.getByRole("button", { name: /view summary/i }));
    await waitFor(() => expect(screen.getByText("75.0%")).toBeTruthy());
    expect(screen.getByText("Demo Student")).toBeTruthy();
  });
});
