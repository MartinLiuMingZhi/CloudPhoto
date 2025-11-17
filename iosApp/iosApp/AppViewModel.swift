import Foundation
import Shared
import Combine

@MainActor
class AppViewModel: ObservableObject {
    @Published var photos: [Photo] = []
    @Published var configs: [StorageConfig] = []
    @Published var defaultConfig: StorageConfig? = nil
    
    private let photoService: PhotoService
    private let configService: ConfigService
    private let albumService: AlbumService
    
    init() {
        let configRepository = ConfigRepository()
        let photoRepository = PhotoRepository()
        let albumRepository = AlbumRepository()
        
        let httpClient = HttpClient()
        self.photoService = PhotoService(
            photoRepository: photoRepository,
            configRepository: configRepository,
            httpClient: httpClient
        )
        self.configService = ConfigService(configRepository: configRepository)
        self.albumService = AlbumService(
            albumRepository: albumRepository,
            photoRepository: photoRepository
        )
        
        loadPhotos()
        loadConfigs()
    }
    
    func loadPhotos() {
        Task {
            do {
                photos = try await photoService.getAllPhotos()
            } catch {
                print("Error loading photos: \(error)")
            }
        }
    }
    
    func loadConfigs() {
        Task {
            do {
                configs = try await configService.getAllConfigs()
                defaultConfig = try await configService.getDefaultConfig()
            } catch {
                print("Error loading configs: \(error)")
            }
        }
    }
    
    func uploadPhoto(photoData: Data, fileName: String, mimeType: String, width: Int32, height: Int32) {
        Task {
            do {
                let kotlinByteArray = KotlinByteArray(size: Int32(photoData.count))
                for (index, byte) in photoData.enumerated() {
                    kotlinByteArray.set(index: Int32(index), value: Int8(bitPattern: byte))
                }
                
                let result = try await photoService.uploadPhoto(
                    photoData: kotlinByteArray,
                    fileName: fileName,
                    mimeType: mimeType,
                    width: width,
                    height: height,
                    configId: nil,
                    albumId: nil
                )
                if result.isSuccess {
                    loadPhotos()
                } else if let error = result.exceptionOrNull() {
                    print("Error uploading photo: \(error)")
                }
            } catch {
                print("Error uploading photo: \(error)")
            }
        }
    }
    
    func deletePhoto(photoId: String) {
        Task {
            do {
                let result = try await photoService.deletePhoto(photoId: photoId)
                if result.isSuccess {
                    loadPhotos()
                } else if let error = result.exceptionOrNull() {
                    print("Error deleting photo: \(error)")
                }
            } catch {
                print("Error deleting photo: \(error)")
            }
        }
    }
    
    func saveConfig(config: StorageConfig) {
        Task {
            do {
                try await configService.saveConfig(config: config)
                loadConfigs()
            } catch {
                print("Error saving config: \(error)")
            }
        }
    }
    
    func deleteConfig(configId: String) {
        Task {
            do {
                try await configService.deleteConfig(id: configId)
                loadConfigs()
            } catch {
                print("Error deleting config: \(error)")
            }
        }
    }
    
    func setDefaultConfig(configId: String) {
        Task {
            do {
                try await configService.setDefaultConfig(id: configId)
                loadConfigs()
            } catch {
                print("Error setting default config: \(error)")
            }
        }
    }
}

