import SwiftUI
import TflArrivals

struct TflSettingsView: View {
    @ObservedObject var viewModel = TflSettingsViewModel()

    @State private var searchQuery: String = ""
    @State private var selectedResult: StopResult?

    @State private var platformFilter: String = ""

    private let directions = ["all", "inbound", "outbound"]
    @State private var directionFilter: String = "all"

    var body: some View {
        Section {
            HStack {
                DebouncingTextField(label: "Station", value: $searchQuery) { value in
                    if value.isEmpty {
                        viewModel.reset()
                    } else {
                        viewModel.performSearch(value)
                    }
                }
                .autocorrectionDisabled()
                Image(systemName: "questionmark.app")
                    .foregroundColor(Color.gray)
                    .help("London Overground, Tube, DLR, and Tram stations are supported. Arrival times are not available at the end of the line.")
            }
            ResultsArea {
                switch viewModel.state {
                case let .data(results):
                    List(results, id: \.self, selection: $selectedResult) { result in
                        Text(result.name)
                    }
                    .onChange(of: selectedResult) { result in
                        if let result {
                            if result.isHub {
                                viewModel.disambiguate(stop: result)
                                selectedResult = nil
                            }
                        }
                    }
                    .listStyle(PlainListStyle())
                case .idle:
                    Text("Search to select station")
                case .empty:
                    Text("No results found")
                case .error:
                    Text("Search error")
                case .loading:
                    ProgressView()
                        .scaleEffect(0.5)
                }
            }

            Picker("Direction", selection: $directionFilter) {
                ForEach(directions, id: \.self) {
                    Text($0)
                }
            }.pickerStyle(.automatic)
            TextField("Platform", text: $platformFilter)
                .autocorrectionDisabled()

            HStack {
                Spacer()
                Button("Save") {
                    if let selectedResult {
                        viewModel.save(
                            stopPoint: selectedResult,
                            platformFilter: platformFilter,
                            directionFilter: directionFilter
                        )
                        NSApp.keyWindow?.close()
                    }
                }.disabled(selectedResult == nil)
                    .buttonStyle(.borderedProminent)
            }
        }.onAppear {
            platformFilter = viewModel.initialPlatform()
            directionFilter = viewModel.initialDirection()
        }
    }
}

private struct ResultsArea<Content: View>: View {
    @ViewBuilder var content: Content

    var body: some View {
        VStack(spacing: 0) {
            content
                .frame(maxWidth: .infinity, maxHeight: .infinity)
        }
        .frame(minHeight: 75, alignment: .center)
    }
}

#Preview {
    Form {
        TflSettingsView()
    }
    .formStyle(.grouped)
}
