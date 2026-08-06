# Search Status

Release status: approved in the v1.0.5 release ledger.

Current evidence:

- Offline search screen and several queries worked.
- Focused verification confirms `2:255` opens the selected ayah.
- Search remains local-only and uses separate normalized search data.

The former exact-anchor failure is historical. Alias improvements such as
`Yaseen`, `Ya-Sin`, and `Yasin` are post-release polish; they must preserve the
display/search separation and be covered by offline search tests.
