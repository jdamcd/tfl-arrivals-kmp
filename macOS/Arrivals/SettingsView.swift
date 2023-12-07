import Combine
import SwiftUI
import TflArrivals

struct SettingsView: View {
    @ObservedObject var viewModel = SettingsViewModel()

    @State private var searchQuery: String = ""
    @State private var selectedResult: StopResult?

    @State private var platformFilter: String = ""

    let directions = ["all", "inbound", "outbound"]
    @State private var directionFilter: String = "all"

    var body: some View {
        VStack(alignment: .leading) {
            HStack {
                Text("Station")
                DebouncingTextField(label: "Search", value: $searchQuery) { value in
                    if !value.isEmpty {
                        viewModel.performSearch(value)
                    }
                }
                .autocorrectionDisabled()
                Image(systemName: "questionmark.app")
                    .foregroundColor(Color.gray)
                    .help("London Overground, Tube, DLR, and Tram stations are supported. Arrival times are not available at the end of the line.")
            }
            switch viewModel.state {
            case let .data(results):
                List(results, id: \.self, selection: $selectedResult) { result in
                    Text(result.name)
                }
                .onChange(of: selectedResult) { result in
                    if let result {
                        if result.isHub() {
                            viewModel.disambiguate(stop: result)
                            selectedResult = nil
                        }
                    }
                }
                .listStyle(PlainListStyle())
            case .idle:
                ResultsArea(text: "Search to select station")
            case .empty:
                ResultsArea(text: "No results found")
            case .error:
                ResultsArea(text: "Search error")
            case .loading:
                ProgressView()
                    .scaleEffect(0.5)
                    .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .center)
                    .background(.background)
            }
            Picker("Direction", selection: $directionFilter) {
                ForEach(directions, id: \.self) {
                    Text($0)
                }
            }.pickerStyle(.segmented)
            HStack {
                Text("Platform")
                TextField("No filter", text: $platformFilter)
                    .autocorrectionDisabled()
            }
            HStack {
                Spacer()
                Button("Save") {
                    if let selectedResult {
                        viewModel.save(
                            stopPoint: selectedResult,
                            platformFilter: platformFilter,
                            directionFilter: directionFilter
                        )
                        NSApplication.shared.keyWindow?.close()
                    }
                }.disabled(selectedResult == nil)
            }
        }.padding()
            .frame(width: 350, height: 250)
            .onAppear {
                platformFilter = viewModel.initialPlatform()
                directionFilter = viewModel.initialDirection()
            }
    }
}

private struct ResultsArea: View {
    var text: String

    var body: some View {
        Text(text)
            .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .center)
            .background(.background)
    }
}

private struct DebouncingTextField: View {
    @State var publisher = PassthroughSubject<String, Never>()
    @State var label: String
    @Binding var value: String
    var valueChanged: ((_ value: String) -> Void)?

    var body: some View {
        TextField(label, text: $value, axis: .vertical)
            .autocorrectionDisabled()
            .onChange(of: value) { value in
                publisher.send(value)
            }
            .onReceive(
                publisher.debounce(
                    for: .seconds(0.7),
                    scheduler: DispatchQueue.main
                )
            ) { value in
                if let valueChanged {
                    valueChanged(value)
                }
            }
    }
}

#Preview {
    SettingsView()
}
