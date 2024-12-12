import Foundation
import TflArrivals

@MainActor
class TflSettingsViewModel: ObservableObject {
    @Published var state: SettingsState = .idle

    private let fetcher = MacDI().tflSearch
    private let settings = MacDI().settings

    func reset() {
        state = .idle
    }

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
        settings.tflPlatform
    }

    func initialDirection() -> String {
        settings.tflDirection
    }

    func save(stopPoint: StopResult, platformFilter: String, directionFilter: String) {
        settings.tflStopName = stopPoint.name
        settings.tflStopId = stopPoint.id
        settings.tflPlatform = platformFilter
        settings.tflDirection = directionFilter
        settings.mode = SettingsConfig().MODE_TFL
    }
}

enum SettingsState: Equatable {
    case idle
    case loading
    case data([StopResult])
    case empty
    case error
}
