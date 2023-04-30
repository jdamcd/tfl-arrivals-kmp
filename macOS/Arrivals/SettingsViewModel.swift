import Foundation
import TflArrivals

@MainActor
class SettingsViewModel: ObservableObject {
    @Published var state: SettingsState = .idle
    @Published var searching = false

    private let fetcher = Arrivals()

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
}

enum SettingsState: Equatable {
    case idle
    case data([StopPoint])
}
