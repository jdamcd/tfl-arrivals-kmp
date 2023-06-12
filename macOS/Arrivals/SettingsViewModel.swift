import Foundation
import TflArrivals

@MainActor
class SettingsViewModel: ObservableObject {
    @Published var state: SettingsState = .idle

    private let fetcher = Arrivals()
    private let settings = Settings()

    func performSearch(_ query: String) {
        state = .loading
        print("Searching: \(query)")
        Task {
            do {
                let result = try await fetcher.searchStops(query: query)
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
    
    func disambiguate(stop: StopResult) {
        state = .loading
        print("Getting stop children: \(stop.id)")
        Task {
            do {
                let result = try await fetcher.stopDetails(id: stop.id)
                print("Result: \(result)")
                if result.children.isEmpty {
                    state = .empty
                } else {
                    state = .data(result.children)
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

    func save(stopPoint: StopResult, platformFilter: String, directionFilter: String) {
        settings.selectedStopName = stopPoint.name
        settings.selectedStopId = stopPoint.id
        settings.platformFilter = platformFilter
        settings.directionFilter = directionFilter
    }
}

enum SettingsState: Equatable {
    case idle
    case data([StopResult])
    case loading
    case empty
    case error
}
