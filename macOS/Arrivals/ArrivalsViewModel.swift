import Foundation
import TflArrivals

@MainActor
class ArrivalsViewModel: ObservableObject {
    @Published var state: ArrivalsState = .idle
    @Published var loading = false

    private let fetcher = Arrivals()

    func load() {
        if !loading {
            loading = true
            Task {
                do {
                    let result = try await fetcher.fetchArrivals()
                    state = .data(result)
                } catch {
                    state = .error
                }
                loading = false
            }
        }
    }
}

enum ArrivalsState: Equatable {
    case idle
    case data(ArrivalsInfo)
    case error
}
