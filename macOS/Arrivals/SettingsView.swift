import Combine
import SwiftUI

struct SettingsView: View {
    @Environment(\.controlActiveState) private var controlActiveState
    @ObservedObject var viewModel = SettingsViewModel()
    @State private var searchQuery: String = ""

    var body: some View {
        VStack(alignment: .leading) {
            DebouncingTextField(label: "Search station", value: $searchQuery) { value in
                if !value.isEmpty {
                    viewModel.performSearch(value)
                }
            }
            .autocorrectionDisabled()
            switch viewModel.state {
            case let .data(results):
                List(results, id: \.id) { result in
                    Text(result.name)
                }
                .listStyle(PlainListStyle())
            case .idle:
                EmptyView()
            }
            Spacer()
        }.padding()
            .frame(width: 300, height: 200)
    }
}

private struct DebouncingTextField: View {
    @State var publisher = PassthroughSubject<String, Never>()
    @State var label: String
    @Binding var value: String
    var valueChanged: ((_ value: String) -> Void)?

    var body: some View {
        TextField(label, text: $value, axis: .vertical)
            .disableAutocorrection(true)
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
