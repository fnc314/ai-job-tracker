# Workday CXS Public Career Site API Reference

## Base Endpoints

Workday exposes a standard candidate experience service (CXS) JSON REST API used by their single-page career applications (`{tenant}.{domain}.myworkdayjobs.com/{site}`).

- **Search Jobs**:
  `POST https://{tenant}.{domain}.myworkdayjobs.com/wday/cxs/{tenant}/{site}/jobs`
  - Header: `Content-Type: application/json`
  - Body:

    ```json
    {
      "appliedFacets": {},
      "limit": 20,
      "offset": 0,
      "searchText": "Android"
    }
    ```

- **Job Detail**:
  `GET https://{tenant}.{domain}.myworkdayjobs.com/wday/cxs/{tenant}/{site}{externalPath}`
  - Example: `GET https://nvidia.wd5.myworkdayjobs.com/wday/cxs/nvidia/NVIDIAExternalCareerSite/job/India-Pune/Senior-Systems-Software-Engineer_JR2021126`

## Target Workday Configurations

A Workday target is identified by:

- `host`: e.g. `nvidia.wd5.myworkdayjobs.com`, `target.wd5.myworkdayjobs.com`
- `tenant`: e.g. `nvidia`, `target`
- `site`: e.g. `NVIDIAExternalCareerSite`, `targetcareers`
