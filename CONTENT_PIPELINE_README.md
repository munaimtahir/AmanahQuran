# Content Pipeline

This tree contains the auditable Quran content reset pipeline for Amanah Quran.

Rules:
- Quran display text is never modified manually.
- Search normalization stays separate.
- Unapproved fonts never enter Android assets.
- Release gates fail until source, checksum, validation, and Trust Center fields are complete.
