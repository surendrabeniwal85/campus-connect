import { useState } from "react";

import DriveList from "./DriveList";

import "./drives.css";

const drivesPages = [{ title: "Upcoming drives", component: DriveList }];

export default function DrivesRoutes() {
  const [activePage, setActivePage] = useState("Upcoming drives");
  const selected = drivesPages.find((page) => page.title === activePage);
  const PageComponent = selected?.component;

  return (
    <section className="drives-module">
      <nav className="drives-navigation" aria-label="Placement pages">
        {drivesPages.map((page) => (
          <button
            key={page.title}
            type="button"
            className={activePage === page.title ? "active" : ""}
            onClick={() => setActivePage(page.title)}
          >
            {page.title}
          </button>
        ))}
      </nav>
      <div className="drives-page-content">{PageComponent && <PageComponent />}</div>
    </section>
  );
}
