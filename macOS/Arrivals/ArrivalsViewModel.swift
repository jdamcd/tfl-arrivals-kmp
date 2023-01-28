import Foundation
import TflArrivals

@MainActor
class ArrivalsViewModel: ObservableObject {
    @Published var state: ArrivalsState = .loading
    
    private let fetcher = Arrivals()
    
    func load() {
        Task {
            do {
                let result = try await fetcher.fetchArrivals()
                state = .data(result)
            } catch {
                state = .error
            }
        }
    }
}

enum ArrivalsState {
    case loading
    case data(ArrivalsInfo)
    case error
}
