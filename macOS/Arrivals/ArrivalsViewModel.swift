import Foundation
import TflArrivals

@MainActor
class ArrivalsViewModel: ObservableObject {
    @Published var state: ArrivalsState = .loading
    
    private let fetcher = Arrivals()
    
    func load() {
        Task {
            let result = try await fetcher.fetchArrivals()
            state = .data(result)
        }
    }
}

enum ArrivalsState {
    case loading
    case data([Arrival])
}
