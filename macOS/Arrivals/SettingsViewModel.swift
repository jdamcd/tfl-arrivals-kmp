import Foundation
import TflArrivals

@MainActor
class SettingsViewModel: ObservableObject {
    @Published var state: SettingsState = .idle

    private let fetcher = ArrivalsBuilder().tflArrivals()
    private let settings = Settings()

    func performSearch(_ query: String) {
        state = .loading
        Task {
            do {
                let result = try await fetcher.searchStops(query: query)
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
        Task {
            do {
                let result = try await fetcher.stopDetails(id: stop.id)
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
    case loading
    case data([StopResult])
    case empty
    case error
}
