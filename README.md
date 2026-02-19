# Time Loop Found Phone Game (MVP)

MVP gry typu **Fake OS** na Androida (Kotlin + Jetpack Compose) z pętlą czasu:

- globalny timer 180 sekund,
- reset ulotnego stanu po timeout,
- trwały notatnik gracza pomiędzy pętlami,
- SMS-y triggerowane przez czas,
- aplikacje: Messages, Notes, Settings, SecureVault,
- warunek zwycięstwa po wpisaniu kodu `1984`.

## Architektura

- `GameViewModel` - główna logika loopa i stanu,
- `ScenarioRepository` - ładowanie JSON narracyjnego,
- Compose UI jako Fake OS.

## Lifecycle

Timer jest pauzowany na `ON_STOP` i wznawiany na `ON_START` przez `ProcessLifecycleOwner`.
