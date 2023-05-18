import Foundation
import TflArrivals

@MainActor
class SettingsViewModel: ObservableObject {
    @Published var state: SettingsState = .idle
    @Published var searching = false
    
    private let fetcher = Arrivals()
    private let settings = Settings()
    
    func performSearch(_ query: String) {
        searching = true
        print("Searching: \(query)")
        Task {
            do {
                let result = try await fetcher.searchStations(query: query)
                print("Result: \(result)")
                state = .data(result)
            } catch {
                print("Load error")
            }
            searching = false
        }
    }
    
    func getPlatformFilter() -> String {
        return settings.platformFilter
    }
    
    func save(stopId: String, platformFilter: String) {
        settings.selectedStop = stopId
        settings.platformFilter = platformFilter
    }
}

enum SettingsState: Equatable {
    case idle
    case data([StopPoint])
}
