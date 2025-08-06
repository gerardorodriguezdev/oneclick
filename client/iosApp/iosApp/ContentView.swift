import SwiftUI
import ComposeApp

struct ComposeView: UIViewControllerRepresentable {
    @EnvironmentObject var appEntrypointWrapper: AppEntrypointWrapper

    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController(appEntrypoint: appEntrypointWrapper.appEntrypoint)
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
    }
}

struct ContentView: View {
    var body: some View {
        ComposeView()
            .ignoresSafeArea(.keyboard)
    }
}

#Preview {
    ContentView()
}
