import SwiftUI
import Shared

/**
 * iOS 路由协调器
 */
class NavigationCoordinator: ObservableObject {
    @Published var currentRoute: AppRoute = .photos
    
    enum AppRoute: String {
        case photos = "photos"
        case albums = "albums"
        case settings = "settings"
    }
    
    func navigate(to route: AppRoute) {
        currentRoute = route
    }
}

/**
 * 路由视图容器
 */
struct NavigationContainer<Content: View>: View {
    @StateObject private var coordinator = NavigationCoordinator()
    let content: (NavigationCoordinator) -> Content
    
    var body: some View {
        content(coordinator)
    }
}

