export default function Header() {
  return (
    <header className="bg-white shadow-sm border-b border-gray-200 flex-shrink-0">
      <div className="container mx-auto px-4 py-4">
        <div className="flex items-center justify-between">
          <div className="flex items-center space-x-2">
            <div className="w-8 h-8 bg-primary-500 rounded-lg flex items-center justify-center">
              <span className="text-white font-bold text-sm">CB</span>
            </div>
            <h1 className="text-xl font-semibold text-gray-800">ChatBot</h1>
          </div>
          <nav className="flex items-center space-x-6">
            <a href="#" className="text-gray-600 hover:text-primary-600 transition-colors">
              Home
            </a>
            <a href="#" className="text-gray-600 hover:text-primary-600 transition-colors">
              About This Project
            </a>
          </nav>
        </div>
      </div>
    </header>
  )
}
