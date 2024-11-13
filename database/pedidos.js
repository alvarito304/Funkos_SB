db.createUser({
    user: 'alvaro',
    pwd: 'alvaro123',
    roles: [
        {
            role: 'readWrite',
            db: 'funkos',
        },
    ],
});


db = db.getSiblingDB('funkos');

db.createCollection('pedidos');

db.pedidos.insertMany([
    {
        _id: ObjectId('64b3fa2e73a1b69e84bce776'),
        idUsuario: 1,
        cliente: {
            nombreCompleto: 'alvaro',
            email: 'alvaro@gmail.com',
            telefono: '+34999999999',
            direccion: {
                calle: 'calle',
                numero: '1',
                ciudad: 'Madrid',
                provincia: 'Madrid',
                pais: 'España',
                codigoPostal: '28054',
            },
        },
        lineasPedido: [
            {
                idProducto: 1,
                precioProducto: 2.99,
                cantidad: 1,
                total: 2.99,
            },
            {
                idProducto: 2,
                precioProducto: 10.00,
                cantidad: 2,
                total: 20.00,
            },
        ],
        createdAt: '2024-09-23T12:57:17.3411925',
        updatedAt: '2024-09-23T12:57:17.3411925',
        isDeleted: false,
        totalItems: 3,
        total: 51.97,
        _class: 'Pedido',
    },
]);