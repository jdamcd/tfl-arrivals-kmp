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
            print("Loading")
            Task {
                do {
                    let result = try await fetcher.fetchArrivals()
                    print("Result: \(result)")
                    state = .data(result)
                } catch {
                    print("Load error")
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
