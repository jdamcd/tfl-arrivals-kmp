import Foundation
import TflArrivals

@MainActor
class SettingsViewModel: ObservableObject {
    @Published var state: SettingsState = .idle
    @Published var searching = false

    private let fetcher = Arrivals()
    private let settings = Settings()

    func performSearch(_ query: String) {
        state = .loading
        searching = true
        print("Searching: \(query)")
        Task {
            do {
                let result = try await fetcher.searchStations(query: query)
                print("Result: \(result)")
                if result.isEmpty {
                    state = .empty
                } else {
                    state = .data(result)
                }
            } catch {
                state = .error
            }
        }
    }

    func initialPlatform() -> String {
        settings.platformFilter
    }

    func initialDirection() -> String {
        settings.directionFilter
    }

    func save(stopPoint: StopPoint, platformFilter: String, directionFilter: String) {
        settings.selectedStopName = stopPoint.name
        settings.selectedStopId = stopPoint.id
        settings.platformFilter = platformFilter
        settings.directionFilter = directionFilter
    }
}

enum SettingsState: Equatable {
    case idle
    case data([StopPoint])
    case loading
    case empty
    case error
}
