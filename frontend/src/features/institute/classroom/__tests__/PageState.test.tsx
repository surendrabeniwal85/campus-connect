import { render, screen } from "@testing-library/react";
import { describe, expect, it, vi } from "vitest";
import { PageState } from "../components/PageState";

describe("PageState", () => {
  it("shows a loading state", () => {
    render(<PageState loading />);
    expect(screen.getByRole("status")).toHaveTextContent(/loading/i);
  });
  it("shows an error and retry action", () => {
    const retry = vi.fn();
    render(<PageState error="Network error" onRetry={retry} />);
    expect(screen.getByRole("alert")).toHaveTextContent("Network error");
    expect(screen.getByRole("button", { name: /retry/i })).toBeTruthy();
  });
  it("shows an empty state", () => {
    render(<PageState empty />);
    expect(screen.getByText(/no records found/i)).toBeTruthy();
  });
});
