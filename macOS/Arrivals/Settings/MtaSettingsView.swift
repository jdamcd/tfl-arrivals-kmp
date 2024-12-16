import SwiftUI
import TflArrivals

struct MtaSettingsView: View {
    @ObservedObject private var viewModel = MtaSettingsViewModel()

    @State private var selectedLine: String
    @State private var selectedStop: String?

    private var lines = Mta().realtime

    init() {
        selectedLine = lines.keys.sorted().first!
    }

    var body: some View {
        Section {
            Picker("Line",
                   selection: $selectedLine) {
                ForEach(lines.keys.sorted(), id: \.self) {
                    Text($0)
                }
                .pickerStyle(.menu)
                .onChange(of: selectedLine) { newValue in
                    selectedStop = nil
                    viewModel.getStops(feedUrl: lines[newValue]!)
                }
            }
            ResultsArea {
                switch viewModel.state {
                case let .data(results):
                    List(results.keys.sorted(), id: \.self, selection: $selectedStop) { key in
                        Text(results[key]!)
                    }
                    .listStyle(PlainListStyle())
                case .idle:
                    Text("Select a line")
                case .error:
                    Text("Error fetching stops")
                case .loading:
                    ProgressView()
                        .scaleEffect(0.5)
                }
            }
            HStack {
                Spacer()
                Button("Save") {
                    viewModel.save(lineUrl: lines[selectedLine]!, stopId: selectedStop!)
                    NSApp.keyWindow?.close()
                }
                .disabled(selectedStop == nil)
                .buttonStyle(.borderedProminent)
            }
        }
    }
}

@MainActor
private class MtaSettingsViewModel: ObservableObject {
    @Published var state: SettingsState = .idle

    private let fetcher = MacDI().gtfsSearch
    private let settings = MacDI().settings

    func getStops(feedUrl: String) {
        if state != .loading {
            state = .loading
            Task {
                do {
                    let result = try await fetcher.getStops(feedUrl: feedUrl)
                    state = .data(result)
                } catch {
                    state = .error
                }
            }
        }
    }

    func save(lineUrl: String, stopId: String) {
        settings.gtfsRealtime = lineUrl
        settings.gtfsSchedule = Mta().SCHEDULE
        settings.gtfsStop = stopId
        settings.mode = SettingsConfig().MODE_GTFS
    }
}

private enum SettingsState: Equatable {
    case idle
    case loading
    case data([String: String])
    case error
}

#Preview {
    Form {
        MtaSettingsView()
    }
    .formStyle(.grouped)
}
