import Combine
import SwiftUI
import TflArrivals

struct SettingsView: View {
    @Environment(\.controlActiveState) private var controlActiveState
    @ObservedObject var viewModel = SettingsViewModel()

    @State private var searchQuery: String = ""
    @State private var selectedResult: StopPoint? = nil

    @State private var platformFilter: String = ""

    var body: some View {
        VStack(alignment: .leading) {
            Text("Station").font(.title2)
            DebouncingTextField(label: "Search station", value: $searchQuery) { value in
                if !value.isEmpty {
                    viewModel.performSearch(value)
                }
            }
            .autocorrectionDisabled()
            switch viewModel.state {
            case let .data(results):
                List(results, id: \.self, selection: $selectedResult) { result in
                    Text(result.name)
                }
                .listStyle(PlainListStyle())
            case .idle:
                EmptyView()
            }
            Spacer()
            Text("Platform filter").font(.title2)
            TextField("", text: $platformFilter)
                .autocorrectionDisabled()

            HStack {
                Spacer()
                Button("Save") {
                    if let id = selectedResult?.id {
                        viewModel.save(stopId: id, platformFilter: platformFilter)
                        NSApplication.shared.keyWindow?.close()
                    }
                }
            }
        }.padding()
            .frame(width: 350, height: 350)
            .onAppear {
                platformFilter = viewModel.getPlatformFilter()
            }
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
                    for: .seconds(1),
                    scheduler: DispatchQueue.main
                )
            ) { value in
                if let valueChanged {
                    valueChanged(value)
                }
            }
    }
}

struct Settings_Previews: PreviewProvider {
    static var previews: some View {
        SettingsView()
    }
}
