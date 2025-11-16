import SwiftUI
import Shared

struct ContentView: View {
    @StateObject private var viewModel = AppViewModel()
    @State private var selectedTab = 0
    
    var body: some View {
        TabView(selection: $selectedTab) {
            PhotosView(viewModel: viewModel)
                .tabItem {
                    Label("照片", systemImage: "photo")
                }
                .tag(0)
            
            AlbumsView()
                .tabItem {
                    Label("相册", systemImage: "photo.on.rectangle")
                }
                .tag(1)
            
            SettingsView(viewModel: viewModel)
                .tabItem {
                    Label("我的", systemImage: "person.circle")
                }
                .tag(2)
        }
    }
}

struct PhotosView: View {
    @ObservedObject var viewModel: AppViewModel
    @State private var showImagePicker = false
    @State private var selectedImage: UIImage?
    @State private var showUploadDialog = false
    
    var body: some View {
        NavigationView {
            if viewModel.photos.isEmpty {
                VStack {
                    Text("暂无照片")
                        .foregroundColor(.secondary)
                    Text("点击右上角按钮拍照")
                        .font(.caption)
                        .foregroundColor(.secondary)
                }
                .navigationTitle("照片")
                .toolbar {
                    ToolbarItem(placement: .navigationBarTrailing) {
                        Button(action: {
                            showImagePicker = true
                        }) {
                            Image(systemName: "camera.fill")
                        }
                    }
                }
            } else {
                ScrollView {
                    LazyVGrid(columns: [GridItem(.flexible()), GridItem(.flexible()), GridItem(.flexible())], spacing: 2) {
                        ForEach(viewModel.photos, id: \.id) { photo in
                            AsyncImage(url: URL(string: photo.url)) { image in
                                image
                                    .resizable()
                                    .aspectRatio(contentMode: .fill)
                            } placeholder: {
                                ProgressView()
                            }
                            .frame(width: UIScreen.main.bounds.width / 3 - 2, height: UIScreen.main.bounds.width / 3 - 2)
                            .clipped()
                        }
                    }
                }
                .navigationTitle("照片")
                .toolbar {
                    ToolbarItem(placement: .navigationBarTrailing) {
                        Button(action: {
                            showImagePicker = true
                        }) {
                            Image(systemName: "camera.fill")
                        }
                    }
                }
            }
        }
        .sheet(isPresented: $showImagePicker) {
            ImagePicker(image: $selectedImage, showUploadDialog: $showUploadDialog)
        }
        .alert("上传照片", isPresented: $showUploadDialog) {
            Button("上传") {
                if let image = selectedImage,
                   let imageData = image.jpegData(compressionQuality: 0.8) {
                    viewModel.uploadPhoto(
                        photoData: imageData,
                        fileName: "photo_\(Date().timeIntervalSince1970).jpg",
                        mimeType: "image/jpeg",
                        width: Int32(image.size.width),
                        height: Int32(image.size.height)
                    )
                }
            }
            Button("取消", role: .cancel) {}
        } message: {
            Text("是否上传这张照片到云端？")
        }
    }
}

struct AlbumsView: View {
    var body: some View {
        NavigationView {
            VStack {
                Text("相册功能开发中...")
                    .foregroundColor(.secondary)
            }
            .navigationTitle("相册")
        }
    }
}

struct SettingsView: View {
    @ObservedObject var viewModel: AppViewModel
    @State private var showAddConfigDialog = false
    
    var body: some View {
        NavigationView {
            if viewModel.configs.isEmpty {
                VStack {
                    Text("暂无存储配置")
                        .foregroundColor(.secondary)
                    Text("点击右下角按钮添加配置")
                        .font(.caption)
                        .foregroundColor(.secondary)
                }
                .navigationTitle("我的")
                .toolbar {
                    ToolbarItem(placement: .navigationBarTrailing) {
                        Button(action: {
                            showAddConfigDialog = true
                        }) {
                            Image(systemName: "plus")
                        }
                    }
                }
            } else {
                List {
                    ForEach(viewModel.configs, id: \.id) { config in
                        StorageConfigRow(
                            config: config,
                            isDefault: config.id == viewModel.defaultConfig?.id,
                            onSetDefault: {
                                viewModel.setDefaultConfig(configId: config.id)
                            },
                            onDelete: {
                                viewModel.deleteConfig(configId: config.id)
                            }
                        )
                    }
                }
                .navigationTitle("我的")
                .toolbar {
                    ToolbarItem(placement: .navigationBarTrailing) {
                        Button(action: {
                            showAddConfigDialog = true
                        }) {
                            Image(systemName: "plus")
                        }
                    }
                }
            }
        }
        .sheet(isPresented: $showAddConfigDialog) {
            AddConfigView(viewModel: viewModel, isPresented: $showAddConfigDialog)
        }
    }
}

struct StorageConfigRow: View {
    let config: StorageConfig
    let isDefault: Bool
    let onSetDefault: () -> Void
    let onDelete: () -> Void
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack {
                Text(config.name)
                    .font(.headline)
                if isDefault {
                    Text("默认")
                        .font(.caption)
                        .padding(.horizontal, 8)
                        .padding(.vertical, 4)
                        .background(Color.blue)
                        .foregroundColor(.white)
                        .cornerRadius(4)
                }
            }
            Text("提供商: \(config.provider.name)")
                .font(.caption)
                .foregroundColor(.secondary)
            Text("Bucket: \(config.bucketName)")
                .font(.caption)
                .foregroundColor(.secondary)
            HStack {
                if !isDefault {
                    Button("设为默认") {
                        onSetDefault()
                    }
                    .buttonStyle(.bordered)
                }
                Button("删除") {
                    onDelete()
                }
                .buttonStyle(.bordered)
                .foregroundColor(.red)
            }
        }
        .padding(.vertical, 4)
    }
}

struct AddConfigView: View {
    @ObservedObject var viewModel: AppViewModel
    @Binding var isPresented: Bool
    @State private var name = ""
    @State private var endpoint = ""
    @State private var accessKeyId = ""
    @State private var accessKeySecret = ""
    @State private var bucketName = ""
    @State private var region = ""
    @State private var isDefault = false
    @State private var selectedProvider: StorageProvider = .aliyunOss
    
    var body: some View {
        NavigationView {
            Form {
                Section("基本信息") {
                    TextField("配置名称", text: $name)
                    Picker("提供商", selection: $selectedProvider) {
                        Text("阿里云OSS").tag(StorageProvider.aliyunOss)
                        Text("AWS S3").tag(StorageProvider.awsS3)
                        Text("腾讯云COS").tag(StorageProvider.tencentCos)
                        Text("MinIO").tag(StorageProvider.minio)
                        Text("自定义S3").tag(StorageProvider.customS3)
                    }
                }
                Section("连接信息") {
                    TextField("Endpoint", text: $endpoint)
                    TextField("Access Key ID", text: $accessKeyId)
                    SecureField("Access Key Secret", text: $accessKeySecret)
                    TextField("Bucket Name", text: $bucketName)
                    TextField("Region (可选)", text: $region)
                }
                Section {
                    Toggle("设为默认", isOn: $isDefault)
                }
            }
            .navigationTitle("添加存储配置")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button("取消") {
                        isPresented = false
                    }
                }
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("保存") {
                        let config = StorageConfig(
                            id: "\(Int(Date().timeIntervalSince1970))_\(Int.random(in: 0...999999))",
                            name: name,
                            provider: selectedProvider,
                            endpoint: endpoint,
                            accessKeyId: accessKeyId,
                            accessKeySecret: accessKeySecret,
                            bucketName: bucketName,
                            region: region.isEmpty ? nil : region,
                            isDefault: isDefault,
                            createdAt: Int64(Date().timeIntervalSince1970)
                        )
                        viewModel.saveConfig(config: config)
                        isPresented = false
                    }
                    .disabled(name.isEmpty || endpoint.isEmpty || accessKeyId.isEmpty || accessKeySecret.isEmpty || bucketName.isEmpty)
                }
            }
        }
    }
}

struct ImagePicker: UIViewControllerRepresentable {
    @Binding var image: UIImage?
    @Binding var showUploadDialog: Bool
    
    func makeUIViewController(context: Context) -> UIImagePickerController {
        let picker = UIImagePickerController()
        picker.delegate = context.coordinator
        picker.sourceType = .camera
        return picker
    }
    
    func updateUIViewController(_ uiViewController: UIImagePickerController, context: Context) {}
    
    func makeCoordinator() -> Coordinator {
        Coordinator(self)
    }
    
    class Coordinator: NSObject, UIImagePickerControllerDelegate, UINavigationControllerDelegate {
        let parent: ImagePicker
        
        init(_ parent: ImagePicker) {
            self.parent = parent
        }
        
        func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey : Any]) {
            if let image = info[.originalImage] as? UIImage {
                parent.image = image
                parent.showUploadDialog = true
            }
            picker.dismiss(animated: true)
        }
        
        func imagePickerControllerDidCancel(_ picker: UIImagePickerController) {
            picker.dismiss(animated: true)
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
