# TODO List for FutbolApp Firebase Integration and Features

## Completed Tasks
- [x] Add Firebase dependencies to app/build.gradle.kts
- [x] Add google-services plugin to app/build.gradle.kts
- [x] Create FirebaseAuthManager.kt for user authentication
- [x] Create FirebaseFirestoreManager.kt for database operations
- [x] Create data models: User, Team, Match, Player, Lineup, Statistics, Field, Improvement, Role, UserRole
- [x] Create AuthRepository.kt
- [x] Create TeamRepository.kt
- [x] Create AuthViewModel.kt
- [x] Create TeamViewModel.kt
- [x] Create RoleViewModel.kt for role management
- [x] Create RoleManager.kt for role assignment logic
- [x] Create RoleManagementScreen.kt with role-based UI
- [x] Create RoleBasedNavigation.kt for navigation permissions
- [x] Create AIAssistant.kt for AI-powered features
- [x] Create AIAssistantScreen.kt
- [x] Create LoginScreen composable
- [x] Update MainActivity to check auth and show login if needed
- [x] Fix compilation errors with @OptIn annotations
- [x] Build project successfully
- [x] Create MatchRepository
- [x] Create PlayerRepository
- [x] Create LineupRepository
- [x] Create StatisticsRepository
- [x] Create FieldRepository
- [x] Create ImprovementRepository

## Pending Tasks
- [ ] Add google-services.json to app/ directory (required for Firebase)
- [ ] Create remaining ViewModels: MatchViewModel, PlayerViewModel, LineupViewModel, StatisticsViewModel, FieldViewModel, ImprovementViewModel
- [ ] Update MiEquipoScreen to use TeamViewModel for creating/editing team and improvements
- [ ] Update ProximoPartidoScreen to show upcoming matches using MatchViewModel
- [ ] Update PartidosScreen to list all matches
- [ ] Update JugadoresScreen to manage players
- [ ] Update AlineacionesScreen to create/edit lineups
- [ ] Update EstadisticasScreen to show stats
- [ ] Update CamposScreen to manage fields
- [ ] Implement roles-based UI restrictions (coach full access, assistant, players, physio, coordinator)
- [ ] Add CRUD operations for all entities
- [ ] Correlate entities (e.g., lineups with matches, stats with players, improvements with team)
- [ ] Test real-time synchronization
- [ ] Build and test the app
